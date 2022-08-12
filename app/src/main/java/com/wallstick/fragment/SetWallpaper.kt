package com.wallstick.fragment

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.Dialog
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.wallstick.BuildConfig
import com.wallstick.R
import com.wallstick.adapters.PhotosAdapter
import com.wallstick.database.LatestPhoto
import com.wallstick.database.PhotoViewModel
import com.wallstick.databinding.FragmentSetWallpaperBinding
import com.wallstick.databinding.WallpaperTypeDialogBinding
import com.wallstick.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class SetWallpaper : Fragment(){

    private lateinit var binding: FragmentSetWallpaperBinding
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.to_bottom_anim) }
    private var clicked = false
    private lateinit var mPhotoViewModel: PhotoViewModel
    private lateinit var currentPhoto: LatestPhoto
//    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var bitmap: Bitmap
    private var file: String = ""
    private lateinit var progressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSetWallpaperBinding.inflate(layoutInflater)
        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        currentPhoto = Utils.currentPhoto
        progressDialog = Dialog(requireContext())
        progressDialog.setContentView(R.layout.progress_circular)
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val isWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        isWritePermissionGranted = isWritePermission || sdkCheck()
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permissions ->
//            isReadPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            if (permissions){
                isWritePermissionGranted = true
            }else{
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), WRITE_EXTERNAL_STORAGE)) {
//                    requestPermission()
                } else {
                    //display error dialog
                    val snackbar = Snackbar.make(requireView(),
                        "Storage Permission is required to store Image to the gallery",
                        Snackbar.LENGTH_LONG)
                    snackbar.setAction("Permission Snackbar",
                        View.OnClickListener {
                            if (activity == null) {
                                return@OnClickListener
                            }
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", requireActivity().packageName, null)
                            intent.data = uri
                            this.startActivity(intent)
                        })
                    snackbar.show()
                }
            }
//            isWritePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
            deletePhotoFromInternalStorage(file)
        }

        if (currentPhoto.isFavourite){
            binding.fabFav.setImageResource(R.drawable.ic_fab_filled_heart)
        }else{
            binding.fabFav.setImageResource(R.drawable.ic_fab_fav)
        }

        binding.lavImageLoading.playAnimation()
        Glide.with(requireContext())
            .asBitmap()
            .load(currentPhoto.originalUrl)
            .into(object: CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource
                    binding.lavImageLoading.pauseAnimation()
                    binding.lavImageLoading.visibility = View.GONE
                    binding.ivWallpaper.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

//        Picasso.get().load(currentPhoto.originalUrl).into(binding.ivWallpaper)

        binding.fabMain.setOnClickListener {
            setVisibility()
            setAnimation()
            clicked = !clicked
        }

        binding.fabFav.setOnClickListener {
            if (currentPhoto.isFavourite){
                toggleFavourite(currentPhoto,false)
                binding.fabFav.setImageResource(R.drawable.ic_fab_fav)
            }else{
                toggleFavourite(currentPhoto,true)
                binding.fabFav.setImageResource(R.drawable.ic_fab_filled_heart)
            }
        }
        binding.fabWallpaper.setOnClickListener {
            showDialogForWallpaper()
        }
        binding.fabDownload.setOnClickListener {
            if(sdkCheck()){
                saveImageToExternalStorage(bitmap)
            }else{
                if (!isWritePermissionGranted){
                    Log.d("CLEAR","No Write")
                    requestPermission()
                }else{
                    Log.d("CLEAR","Is Write")
                    saveImageToExternalStorage(bitmap)
                }
            }
        }
        binding.fabShare.setOnClickListener {
            progressDialog.show()
            lifecycleScope.launch{
                shareImage()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (file.isNotEmpty()){
            try {
                deletePhotoFromInternalStorage(file)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            deletePhotoFromInternalStorage(file)
        }
    }

    private suspend fun shareImage() {
        withContext(Dispatchers.IO){
            val date = Date()
            val format: String = DateFormat.format("MM-dd-yyyy_hh:mm:ss", date).toString()
            savePhotoToInternalStorage(format,bitmap)
        }
    }

    private fun deletePhotoFromInternalStorage(filename: String): Boolean {
        return try {
            val dir: File = requireContext().filesDir
            val file = File(dir, filename)
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun savePhotoToInternalStorage(filename: String, bmp: Bitmap) {
        withContext(Dispatchers.IO){
            try {
                requireContext().openFileOutput("$filename.jpg", AppCompatActivity.MODE_PRIVATE).use { stream ->
                    if (!bmp.compress(Bitmap.CompressFormat.PNG,100,stream)){
                        progressDialog.dismiss()
                        throw IOException("Couldn't save Image")
                    }
                    loadPhotoFromInternalStorage(filename)
                }
            }catch (e: Exception){
                e.printStackTrace()
                progressDialog.dismiss()
                Log.d("CLEAR","msg: ${e.message}")
            }
        }
    }

    private suspend fun loadPhotoFromInternalStorage(fileName: String){
        try {
            withContext(Dispatchers.IO){
                val files = requireContext().filesDir.listFiles()
                files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") && it.name.equals("$fileName.jpg") }?.map {
                    val photoUri = FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider",it)
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/jpg"
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    intent.putExtra(Intent.EXTRA_STREAM, photoUri)
                    intent.putExtra(Intent.EXTRA_TEXT,"Found some amazing Wallpapers from this platform\n\nYou can also give it a try by downloading it from here\nWallstick Application:\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                    progressDialog.dismiss()
                    resultLauncher.launch(Intent.createChooser(intent,"Share Image"))
                    file = "$fileName.jpg"
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            progressDialog.dismiss()
        }
    }

    private fun saveImageToExternalStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requireContext().contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Toast.makeText(requireContext() , "Saved to Gallery" , Toast.LENGTH_SHORT).show()
            }
        }catch (e: IOException){
            e.printStackTrace()
            Toast.makeText(requireContext() , "Download Failed" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun sdkCheck(): Boolean{
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    private fun hasReadPermission(): Boolean {
        return  EasyPermissions.hasPermissions(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    private fun hasWritePermission(): Boolean {
        return  EasyPermissions.hasPermissions(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun requestPermission(){
//        val isReadPermission = ContextCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_DENIED

        val isWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

//        isReadPermissionGranted = isReadPermission
        isWritePermissionGranted = isWritePermission || sdkCheck()

        val permissionRequest = mutableListOf<String>()
        if (!isWritePermissionGranted){
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
//        if (!isReadPermissionGranted){
//            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
        if (permissionRequest.isNotEmpty()){
            permissionLauncher.launch(WRITE_EXTERNAL_STORAGE)
        }
    }

    fun toggleFavourite(latestPhoto: LatestPhoto,isFavourite: Boolean){
        val updatePhoto = LatestPhoto(
            photoId = latestPhoto.photoId,
            previewUrl = latestPhoto.previewUrl,
            originalUrl = latestPhoto.originalUrl,
            isFavourite = isFavourite,
            isLatest = latestPhoto.isLatest,
            isTrending = latestPhoto.isTrending,
            tagList = latestPhoto.tagList
        )
        currentPhoto = updatePhoto
//        val currentFavourite = PhotosAdapter.oldLatestPhotoList.find { it.photoId == latestPhoto.photoId }?.isFavourite
//        PhotosAdapter.oldLatestPhotoList.find { it.photoId == latestPhoto.photoId }?.isFavourite = !currentFavourite!!
//        val currentFavourite = PhotosAdapter.oldLatestPhotoList[Utils.currentIndex].isFavourite
//        PhotosAdapter.oldLatestPhotoList[Utils.currentIndex].isFavourite = !currentFavourite
//        LatestFragment.adapter.notifyItemChanged(Utils.currentIndex)
        mPhotoViewModel.updateLatestPhoto(updatePhoto)
    }

    private fun setVisibility() {
        if (!clicked){
            binding.fabWallpaper.visibility = View.VISIBLE
            binding.fabFav.visibility = View.VISIBLE
            binding.fabShare.visibility = View.VISIBLE
            binding.fabDownload.visibility = View.VISIBLE
        }else{
            binding.fabWallpaper.visibility = View.GONE
            binding.fabFav.visibility = View.GONE
            binding.fabShare.visibility = View.GONE
            binding.fabDownload.visibility = View.GONE
        }
    }

    private fun setAnimation() {
        if (!clicked){
            binding.fabWallpaper.startAnimation(fromBottom)
            binding.fabFav.startAnimation(fromBottom)
            binding.fabShare.startAnimation(fromBottom)
            binding.fabDownload.startAnimation(fromBottom)
            binding.fabMain.startAnimation(rotateOpen)
        }else{
            binding.fabWallpaper.startAnimation(toBottom)
            binding.fabFav.startAnimation(toBottom)
            binding.fabShare.startAnimation(toBottom)
            binding.fabDownload.startAnimation(toBottom)
            binding.fabMain.startAnimation(rotateClose)
        }
    }

    private fun showDialogForWallpaper(){
        val wallpaperTypeList = listOf("Set to HomeScreen", "Set to LockScreen", "Set to Both").toTypedArray()
        var checkedItem = 0
        val wallpaperTypeDialogBinding = WallpaperTypeDialogBinding.bind(LayoutInflater.from(requireContext()).inflate(R.layout.wallpaper_type_dialog, binding.root, false))
        wallpaperTypeDialogBinding.rbHomeScreen.isChecked = true
        val dialog = MaterialAlertDialogBuilder(requireContext()).setView(wallpaperTypeDialogBinding.root).create()
        dialog.show()
        wallpaperTypeDialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        wallpaperTypeDialogBinding.btnSet.setOnClickListener {
            if (wallpaperTypeDialogBinding.rbHomeScreen.isChecked){
                checkedItem = 0
                setWallPaper(flagSystem = true, flagLock = false)
                dialog.dismiss()
            }else if(wallpaperTypeDialogBinding.rbLockScreen.isChecked){
                checkedItem = 1
                setWallPaper(flagSystem = false, flagLock = true)
                dialog.dismiss()
            }else if(wallpaperTypeDialogBinding.rbBothScreen.isChecked){
                checkedItem = 2
                setWallPaper(flagSystem = true, flagLock = true)
                dialog.dismiss()
            }
        }
    }

    private fun setWallPaper(flagSystem: Boolean, flagLock: Boolean){
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val wallpaperHeight: Int = Resources.getSystem().displayMetrics.heightPixels
            val wallpaperWidth: Int = Resources.getSystem().displayMetrics.widthPixels
            val start = Point(0, 0)
            val end = Point(bitmap.width, bitmap.height)
            if (bitmap.width > wallpaperWidth) {
                start.x = (bitmap.width - wallpaperWidth) / 2
                end.x = start.x + wallpaperWidth
            }
            if (bitmap.height > wallpaperHeight) {
                start.y = (bitmap.height - wallpaperHeight) / 2
                end.y = start.y + wallpaperHeight
            }
            if (flagSystem){
                wallpaperManager.setBitmap(bitmap, Rect(start.x, start.y, end.x, end.y), false, WallpaperManager.FLAG_SYSTEM)
            }
            if (flagLock){
                wallpaperManager.setBitmap(bitmap, Rect(start.x, start.y, end.x, end.y), false, WallpaperManager.FLAG_LOCK)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
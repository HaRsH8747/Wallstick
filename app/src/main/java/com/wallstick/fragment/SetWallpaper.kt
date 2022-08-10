package com.wallstick.fragment

import android.Manifest
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wallstick.R
import com.wallstick.database.LatestPhoto
import com.wallstick.database.PhotoViewModel
import com.wallstick.databinding.FragmentSetWallpaperBinding
import com.wallstick.databinding.WallpaperTypeDialogBinding
import com.wallstick.utils.Utils
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class SetWallpaper : Fragment(){

    private lateinit var binding: FragmentSetWallpaperBinding
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.to_bottom_anim) }
    private var clicked = false
    private lateinit var mPhotoViewModel: PhotoViewModel
    private lateinit var currentPhoto: LatestPhoto
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var bitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSetWallpaperBinding.inflate(layoutInflater)
        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        currentPhoto = Utils.currentLatestPhoto
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
            isReadPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            isWritePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        if (currentPhoto.isFavourite){
            binding.fabFav.setImageResource(R.drawable.ic_fab_filled_heart)
        }else{
            binding.fabFav.setImageResource(R.drawable.ic_fab_fav)
        }

        Glide.with(requireContext())
            .asBitmap()
            .load(currentPhoto.originalUrl)
            .into(object: CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource
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
                binding.fabFav.setImageResource(R.drawable.ic_fab_fav)
            }else{
                binding.fabFav.setImageResource(R.drawable.ic_fab_filled_heart)
            }
            currentPhoto.isFavourite = !currentPhoto.isFavourite
            toggleFavourite(currentPhoto)
        }
        binding.fabWallpaper.setOnClickListener {
            showDialogForWallpaper()
        }
        binding.fabDownload.setOnClickListener {
            if(sdkCheck()){
                saveImageToExternalStorage(bitmap)
            }else{
                if (!isWritePermissionGranted){
                    requestPermission()
                }else{
                    saveImageToExternalStorage(bitmap)
                }
            }
        }
        binding.fabShare.setOnClickListener {

        }

        return binding.root
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
        val isReadPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED

        val isWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED

        isReadPermissionGranted = isReadPermission
        isWritePermissionGranted = isWritePermission || sdkCheck()

        val permissionRequest = mutableListOf<String>()
        if (!isWritePermissionGranted){
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!isReadPermissionGranted){
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionRequest.isNotEmpty()){
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    fun toggleFavourite(latestPhoto: LatestPhoto){
        val updatePhoto = LatestPhoto(
            photoId = latestPhoto.photoId,
            previewUrl = latestPhoto.previewUrl,
            originalUrl = latestPhoto.originalUrl,
            isFavourite = !latestPhoto.isFavourite,
            isLatest = latestPhoto.isLatest,
            isTrending = latestPhoto.isTrending,
            tagList = latestPhoto.tagList
        )
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
package com.wallstick.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.wallstick.R
import com.wallstick.database.LatestPhoto
import com.wallstick.database.PhotoViewModel
import com.wallstick.databinding.PhotoItemBinding
import com.wallstick.utils.Utils


class PhotosAdapter(
    val context: Context,
    val mPhotoViewModel: PhotoViewModel,
    val isFavouriteFragment: Boolean
): RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {

    companion object{
        var oldLatestPhotoList = mutableListOf<LatestPhoto>()
    }
    private var insertIndex: Int = 0
    private lateinit var bitmap: Bitmap

    //    var photosListPixabay: PixabayResponse
//    init {
//        this.photosListPixabay = photosListPixabay
//    }

    inner class PhotosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var photoItemBinding: PhotoItemBinding

        init {
            photoItemBinding = PhotoItemBinding.bind(itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        return PhotosViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
//        val item = Utils.flickrResponse.photos.photo[position]
//        val url = createURL(item.farm,item.id,i   tem.secret)

        val photo = oldLatestPhotoList[position]
        if (isFavouriteFragment){
            holder.photoItemBinding.ivFavourite.visibility = View.VISIBLE
            if (photo.isFavourite){
                Log.d("CLEAR","pos $position")
                holder.photoItemBinding.ivFavourite.setImageResource(R.drawable.heart)
            }
        }else{
            holder.photoItemBinding.ivFavourite.visibility = View.GONE
        }

        if (position % 2 == 0){
            setMargins(holder.photoItemBinding.cvWallpaper,getDp(15),getDp(15),getDp(0),getDp(15))
        }else{
            setMargins(holder.photoItemBinding.cvWallpaper,getDp(0),getDp(15),getDp(15),getDp(15))
        }
        holder.photoItemBinding.lavImageLoading.playAnimation()
        Glide.with(context)
            .asBitmap()
            .load(photo.previewUrl)
            .placeholder(R.drawable.app_logo)
            .error(R.drawable.close)
            .into(object: CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource
                    holder.photoItemBinding.ivPhoto.setImageBitmap(resource)
                    holder.photoItemBinding.lavImageLoading.pauseAnimation()
                    holder.photoItemBinding.lavImageLoading.visibility = View.GONE
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
//        Picasso.get().load(photo.previewUrl)
//            .placeholder(R.drawable.app_logo)
//            .error(R.drawable.close)
//            .fit()
//            .centerCrop()
//            .into(holder.photoItemBinding.ivPhoto)
        holder.photoItemBinding.ivFavourite.setOnClickListener {
            Log.d("CLEAR","clicked pos $position")
            toggleFavourite(photo)
            notifyItemChanged(position)
//            if (photo.isFavourite){
//                holder.photoItemBinding.ivFavourite.setImageResource(R.drawable.heart_outline)
//            }else{
//                holder.photoItemBinding.ivFavourite.setImageResource(R.drawable.heart)
//            }
        }
        holder.photoItemBinding.cvWallpaper.setOnClickListener { v ->
            Utils.currentPhoto = photo
            Utils.currentIndex = position
            try {
                v.findNavController().navigate(R.id.action_wallpaperFragment_to_setWallpaper)
            }catch (e: IllegalArgumentException){
                v.findNavController().navigate(R.id.action_trendingPhotosFragment_to_setWallpaper)
            }
        }
//        for (photo in photosListPixabay.hits){
//            val url = createURL(photo.farm,photo.id,photo.secret)
//            Log.d("CLEAR","photo: ${photo.id}")
//            Glide.with(context).load(photo.previewURL).into(holder.ivPhoto)
//            Log.d("CLEAR","url: ${photo.largeImageURL}")
//            Log.d("CLEAR","photoURL: ${photo.webformatURL}")
//        }
    }


    override fun getItemCount(): Int {
        return oldLatestPhotoList.size
    }

    fun getDp(i: Int): Int{
        return (i * Resources.getSystem().displayMetrics.density + 0.5F).toInt()
    }

    fun setMargins(v: View, l: Int, t: Int, r: Int, b: Int) {
        if (v.layoutParams is MarginLayoutParams) {
            val p = v.layoutParams as MarginLayoutParams
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }

    private fun createURL(farm: Int, id: String, secret: String): String{
        return "https://farm$farm.staticflickr.com/$farm/${id}_$secret.jpg"
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newLatestPhotoList: List<LatestPhoto>, fromSearch: Boolean){
        if (fromSearch){
            insertIndex = oldLatestPhotoList.size
            Log.d("CLEAR","insertIndex: $insertIndex, oldsize: ${oldLatestPhotoList.size}")
            oldLatestPhotoList.clear()
            oldLatestPhotoList.addAll(newLatestPhotoList)
            notifyItemRangeInserted(insertIndex,oldLatestPhotoList.size)
//            notifyDataSetChanged()
        }else{
            val diffUtil = LatestDiffUtil(oldLatestPhotoList, newLatestPhotoList)
            val diffResults = DiffUtil.calculateDiff(diffUtil)
            oldLatestPhotoList = newLatestPhotoList.toMutableList()
//            oldLatestPhotoList.addAll(newLatestPhotoList)
            diffResults.dispatchUpdatesTo(this)
        }
//        this.oldLatestPhotoList = newLatestPhotoList
//        notifyDataSetChanged()
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
//        val currentFav = oldLatestPhotoList.find { it.photoId == latestPhoto.photoId }?.isFavourite
//        oldLatestPhotoList.find { it.photoId == latestPhoto.photoId }?.isFavourite = !currentFav!!
        mPhotoViewModel.updateLatestPhoto(updatePhoto)
    }
}
package com.wallstick.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wallstick.R
import com.wallstick.database.LatestPhoto
import com.wallstick.database.PhotoViewModel
import com.wallstick.databinding.PhotoItemBinding
import com.wallstick.utils.Utils


class PhotosAdapter(
    val context: Context,
    val mPhotoViewModel: PhotoViewModel,
): RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {

    private var oldLatestPhotoList = emptyList<LatestPhoto>()

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
        if (photo.isFavourite){
            Log.d("CLEAR","pos $position")
            holder.photoItemBinding.ivFavourite.setImageResource(R.drawable.heart)
        }
        Glide.with(context)
            .load(photo.previewUrl)
            .placeholder(R.drawable.app_logo)
            .error(R.drawable.close)
            .into(holder.photoItemBinding.ivPhoto)
//        Picasso.get().load(photo.previewUrl)
//            .placeholder(R.drawable.app_logo)
//            .error(R.drawable.close)
//            .fit()
//            .centerCrop()
//            .into(holder.photoItemBinding.ivPhoto)
        holder.photoItemBinding.ivFavourite.setOnClickListener {
            if (photo.isFavourite){
                holder.photoItemBinding.ivFavourite.setImageResource(R.drawable.heart_outline)
            }else{
                holder.photoItemBinding.ivFavourite.setImageResource(R.drawable.heart)
            }
            toggleFavourite(photo)
        }
        holder.photoItemBinding.cvWallpaper.setOnClickListener { v ->
            Utils.currentLatestPhoto = photo
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

    private fun createURL(farm: Int, id: String, secret: String): String{
        return "https://farm$farm.staticflickr.com/$farm/${id}_$secret.jpg"
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newLatestPhotoList: List<LatestPhoto>, fromSearch: Boolean){
        if (fromSearch){
            this.oldLatestPhotoList = newLatestPhotoList
            notifyDataSetChanged()
        }else{
            val diffUtil = LatestDiffUtil(oldLatestPhotoList, newLatestPhotoList)
            val diffResults = DiffUtil.calculateDiff(diffUtil)
            oldLatestPhotoList = newLatestPhotoList
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
        mPhotoViewModel.updateLatestPhoto(updatePhoto)
    }
}
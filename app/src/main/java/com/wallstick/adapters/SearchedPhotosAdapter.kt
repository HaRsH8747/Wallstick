package com.wallstick.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.squareup.picasso.Picasso
import com.wallstick.R
import com.wallstick.database.LatestPhoto
import com.wallstick.database.PhotoViewModel
import com.wallstick.databinding.PhotoItemBinding
import com.wallstick.utils.Utils

class SearchedPhotosAdapter(
    val context: Context,
    val mPhotoViewModel: PhotoViewModel,
): RecyclerView.Adapter<SearchedPhotosAdapter.SearchedPhotosViewHolder>() {

    private var searchedPhotoList = mutableListOf<LatestPhoto>()
    private lateinit var bitmap: Bitmap

    inner class SearchedPhotosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var photoItemBinding: PhotoItemBinding

        init {
            photoItemBinding = PhotoItemBinding.bind(itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedPhotosViewHolder {
        return SearchedPhotosViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchedPhotosViewHolder, position: Int) {

        val photo = searchedPhotoList[position]
        holder.photoItemBinding.ivFavourite.visibility = View.GONE
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

//        Glide.with(context)
//            .load(searchedPhotoList[position].previewUrl)
//            .placeholder(R.drawable.app_logo)
//            .error(R.drawable.close)
//            .into(holder.photoItemBinding.ivPhoto)
//        holder.photoItemBinding.ivFavourite.visibility = View.GONE

        holder.photoItemBinding.cvWallpaper.setOnClickListener { v ->
            Utils.currentPhoto = photo
            Utils.currentIndex = position
            v.findNavController().navigate(R.id.action_searchFragment_to_setWallpaper)
        }
//        Picasso.get().load(searchedPhotoList[position].previewUrl)
//            .placeholder(R.drawable.app_logo)
//            .error(R.drawable.close)
//            .fit()
//            .centerCrop()
//            .into(holder.photoItemBinding.ivPhoto)
    }

    override fun getItemCount(): Int {
        return searchedPhotoList.size
    }

    fun getDp(i: Int): Int{
        return (i * Resources.getSystem().displayMetrics.density + 0.5F).toInt()
    }

    fun setMargins(v: View, l: Int, t: Int, r: Int, b: Int) {
        if (v.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = v.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(searchedPhoto: List<LatestPhoto>, isUniqueKeyword: Boolean){
        val insertIndex = searchedPhotoList.size
        searchedPhotoList.clear()
        searchedPhotoList.addAll(searchedPhoto.toMutableList())
        if (isUniqueKeyword){
            notifyDataSetChanged()
        }else{
            notifyItemRangeInserted(insertIndex, searchedPhotoList.size)
        }
    }
}
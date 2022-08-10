package com.wallstick.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.wallstick.R
import com.wallstick.databinding.PhotoItemBinding

class SearchedPhotosAdapter(
    val context: Context,
): RecyclerView.Adapter<SearchedPhotosAdapter.SearchedPhotosViewHolder>() {

    private var searchedPhotoList = emptyList<com.wallstick.database.LatestPhoto>()

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
        Glide.with(context)
            .load(searchedPhotoList[position].previewUrl)
            .placeholder(R.drawable.app_logo)
            .error(R.drawable.close)
            .into(holder.photoItemBinding.ivPhoto)
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

    @SuppressLint("NotifyDataSetChanged")
    fun setData(searchedPhoto: List<com.wallstick.database.LatestPhoto>){
        this.searchedPhotoList = searchedPhoto
        notifyDataSetChanged()
    }
}
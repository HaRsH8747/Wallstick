package com.wallstick.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wallstick.R
import com.wallstick.database.PhotoViewModel
import com.wallstick.database.TrendingTag
import com.wallstick.databinding.TrendingItemBinding
import com.wallstick.utils.Utils

class TrendingTagAdapter(
    val context: Context,
    val mPhotoViewModel: PhotoViewModel,
): RecyclerView.Adapter<TrendingTagAdapter.TrendingTagViewHolder>() {

    private var trendingTagList = emptyList<TrendingTag>()

    inner class TrendingTagViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var trendingItemBinding: TrendingItemBinding

        init {
            trendingItemBinding = TrendingItemBinding.bind(itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingTagViewHolder {
        return TrendingTagViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.trending_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TrendingTagViewHolder, position: Int) {
        val tag = trendingTagList[position]
        holder.trendingItemBinding.tvCategory.text = tag.tagText

        Glide.with(context)
            .load(tag.url)
            .placeholder(R.drawable.app_logo)
            .error(R.drawable.close)
            .into(holder.trendingItemBinding.ivCategory)

        holder.trendingItemBinding.cvTrendingTag.setOnClickListener {
            Utils.currentTrendingTag = holder.trendingItemBinding.tvCategory.text.toString()
            it.findNavController().navigate(R.id.action_wallpaperFragment_to_trendingPhotosFragment)
        }

    }

    override fun getItemCount(): Int {
        return trendingTagList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(trendingTagList: List<TrendingTag>){
        this.trendingTagList = trendingTagList
        notifyDataSetChanged()
    }

}
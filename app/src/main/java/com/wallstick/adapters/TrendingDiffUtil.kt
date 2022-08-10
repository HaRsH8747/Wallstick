package com.wallstick.adapters

import androidx.recyclerview.widget.DiffUtil
import com.wallstick.database.TrendingPhoto

class TrendingDiffUtil(
    private val oldList: List<TrendingPhoto>,
    private val newList: List<TrendingPhoto>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].photoId == newList[newItemPosition].photoId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            oldList[oldItemPosition].photoId != newList[newItemPosition].photoId -> {
                false
            }
            oldList[oldItemPosition].previewUrl != newList[newItemPosition].previewUrl -> {
                false
            }
            oldList[oldItemPosition].originalUrl != newList[newItemPosition].originalUrl -> {
                false
            }
            oldList[oldItemPosition].isFavourite != newList[newItemPosition].isFavourite -> {
                false
            }
            oldList[oldItemPosition].tagList != newList[newItemPosition].tagList -> {
                false
            }
            else -> true
        }
    }
}
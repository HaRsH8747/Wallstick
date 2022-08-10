package com.wallstick.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trending_photo_table")
data class TrendingPhoto(
//    val id: Int,
    @PrimaryKey(autoGenerate = true)
    val photoId: Long,
    val previewUrl: String,
    val originalUrl: String,
    val isFavourite: Boolean,
    val tagList: String
)
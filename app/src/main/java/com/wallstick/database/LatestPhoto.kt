package com.wallstick.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "latest_photo_table")
data class LatestPhoto(
//    val id: Int,
    @PrimaryKey(autoGenerate = true)
    val photoId: Long,
    val previewUrl: String,
    val originalUrl: String,
    var isFavourite: Boolean,
    val isLatest: Boolean,
    val isTrending: Boolean,
    val tagList: String
)
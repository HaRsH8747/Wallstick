package com.wallstick.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trending_tag")
data class TrendingTag(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val tagText: String,
    val url: String
)
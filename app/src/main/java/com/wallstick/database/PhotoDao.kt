package com.wallstick.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLatestPhoto(latestPhoto: LatestPhoto)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun addTrendingPhoto(trendingPhoto: TrendingPhoto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrendingTag(trendingTag: TrendingTag)

    @Update
    suspend fun updateLatestPhoto(latestPhoto: LatestPhoto)

    @Query("SELECT * FROM latest_photo_table")
    fun readAllLatestPhotos(): LiveData<List<LatestPhoto>>

//    @Query("SELECT * FROM trending_photo_table")
//    fun readAllTrendingPhotos(): LiveData<List<TrendingPhoto>>

    @Query("SELECT * FROM latest_photo_table WHERE isFavourite = 1")
    fun readFavourites(): LiveData<List<LatestPhoto>>

    @Query("SELECT * FROM trending_tag")
    fun readAllTrendingTags(): LiveData<List<TrendingTag>>
}
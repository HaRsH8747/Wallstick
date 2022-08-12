package com.wallstick.database

import androidx.lifecycle.LiveData

class PhotoRepository(private val photoDao: PhotoDao) {

    val readAllLatestPhotos: LiveData<List<LatestPhoto>> = photoDao.readAllLatestPhotos()
    val readAllPhotos: LiveData<List<LatestPhoto>> = photoDao.readAllPhotos()
//    val readAllTrendingPhotos: LiveData<List<TrendingPhoto>> = photoDao.readAllTrendingPhotos()
    val readFavourites: LiveData<List<LatestPhoto>> = photoDao.readFavourites()
    val readAllTrendingTag: LiveData<List<TrendingTag>> = photoDao.readAllTrendingTags()

    suspend fun updateLatestPhoto(latestPhoto: LatestPhoto){
        photoDao.updateLatestPhoto(latestPhoto)
    }

//    suspend fun updateTrendingPhoto(trendingPhoto: TrendingPhoto){
//        photoDao.updateTrendingPhoto(trendingPhoto)
//    }

    suspend fun addLatestPhoto(latestPhoto: LatestPhoto){
        photoDao.addLatestPhoto(latestPhoto)
    }

//    suspend fun addTrendingPhoto(trendingPhoto: TrendingPhoto){
//        photoDao.addTrendingPhoto(trendingPhoto)
//    }

    suspend fun addTrendingTag(trendingTag: TrendingTag){
        photoDao.addTrendingTag(trendingTag)
    }
}
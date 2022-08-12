package com.wallstick.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoViewModel(application: Application): AndroidViewModel(application) {

    val readAllLatestPhotos: LiveData<List<LatestPhoto>>
    val readAllPhotos: LiveData<List<LatestPhoto>>
//    val readAllTrendingPhotos: LiveData<List<TrendingPhoto>>
    val readFavourites: LiveData<List<LatestPhoto>>
    val readAllTrendingTag: LiveData<List<TrendingTag>>
    private val repository: PhotoRepository

    init {
        val photoDao = PhotosDatabase.getDatabase(application).photoDao()
        repository = PhotoRepository(photoDao)
        readAllLatestPhotos = repository.readAllLatestPhotos
        readAllPhotos = repository.readAllPhotos
//        readAllTrendingPhotos = repository.readAllTrendingPhotos
        readFavourites = repository.readFavourites
        readAllTrendingTag = repository.readAllTrendingTag
    }

    fun updateLatestPhoto(latestPhoto: LatestPhoto){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLatestPhoto(latestPhoto)
        }
    }

    fun addLatestPhoto(latestPhoto: LatestPhoto){
        viewModelScope.launch(Dispatchers.IO){
            repository.addLatestPhoto(latestPhoto)
        }
    }

//    fun addTrendingPhoto(trendingPhoto: TrendingPhoto){
//        viewModelScope.launch(Dispatchers.IO){
//            repository.addTrendingPhoto(trendingPhoto)
//        }
//    }

    fun addTrendingTag(trendingTag: TrendingTag){
        viewModelScope.launch(Dispatchers.IO){
            repository.addTrendingTag(trendingTag)
        }
    }
}
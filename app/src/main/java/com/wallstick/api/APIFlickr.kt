package com.wallstick.api

import com.wallstick.models.flickr.FlickrResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIFlickr {

    @GET("services/rest/")
    fun getRecentPhotos(
        @Query("method")
        method: String = "flickr.photos.getRecent",
        @Query("format")
        format: String = "json",
        @Query("nojsoncallback")
        nojsoncallback: Int = 1,
        @Query("api_key")
        api_key: String = "4232c1825af43ebe055f6a2b8a930b85",
        @Query("orientation")
        orientation: String = "portrait"
    ): Call<FlickrResponse>
}
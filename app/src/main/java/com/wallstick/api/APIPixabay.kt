package com.wallstick.api

import com.wallstick.models.pixabay.PixabayResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIPixabay {


    @GET("api/")
    fun getPhotos(
        @Query("key")
        key: String = "28969364-4e6b8a55dfe6ee95f52c14af7",
        @Query("orientation")
        orientation: String = "vertical",
        @Query("per_page")
        per_page: Int,
        @Query("editors_choice")
        editors_choice: Boolean = true,
        @Query("page")
        page: Int,
        @Query("order")
        order: String,
        @Query("safesearch")
        safesearch: Boolean = true
//        @Query("safe_search")
//        safe_search:Boolean =  true
    ): Call<PixabayResponse>

    @GET("api/")
    fun getSearchedPhotos(
        @Query("key")
        key: String = "28969364-4e6b8a55dfe6ee95f52c14af7",
        @Query("q")
        q: String,
        @Query("orientation")
        orientation: String = "vertical",
        @Query("per_page")
        per_page: Int,
        @Query("page")
        page: Int,
        @Query("safesearch")
        safesearch: Boolean = true
//        @Query("safe_search")
//        safe_search:Boolean =  true
    ): Call<PixabayResponse>
}
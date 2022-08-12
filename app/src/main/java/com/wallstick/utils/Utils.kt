package com.wallstick.utils

import com.wallstick.BuildConfig
import com.wallstick.database.LatestPhoto
import com.wallstick.models.flickr.FlickrResponse
import com.wallstick.models.pixabay.PixabayResponse

class Utils {
    companion object{
//        const val SEARCH_URL = "https://api.pexels.com/"
        const val FLICKR_URL = "https://api.flickr.com/"
        const val PIXABAY_URL = "https://pixabay.com/"
        lateinit var flickrResponse: FlickrResponse
        lateinit var pixabayResponse: PixabayResponse
        lateinit var currentPhoto: LatestPhoto
        lateinit var currentTrendingTag: String
        var currentIndex: Int = 0
        val API_KEY = BuildConfig.API_KEY
    }
}
package com.wallstick.utils

import com.wallstick.database.LatestPhoto
import com.wallstick.database.TrendingPhoto
import com.wallstick.models.flickr.FlickrResponse
import com.wallstick.models.pixabay.PixabayResponse

class Utils {
    companion object{
//        const val SEARCH_URL = "https://api.pexels.com/"
        const val FLICKR_URL = "https://api.flickr.com/"
        const val PIXABAY_URL = "https://pixabay.com/"
        lateinit var flickrResponse: FlickrResponse
        lateinit var pixabayResponse: PixabayResponse
        lateinit var currentLatestPhoto: LatestPhoto
        lateinit var currentTrendingPhoto: TrendingPhoto
        lateinit var currentTrendingTag: String
    }
}
package com.wallstick.api

import com.google.gson.GsonBuilder
import com.wallstick.utils.Utils.Companion.FLICKR_URL
import com.wallstick.utils.Utils.Companion.PIXABAY_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {
    companion object{

        var gson = GsonBuilder()
            .setLenient()
            .create()

        private val retrofit by lazy {
           val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(logging).build()
            Retrofit.Builder()
                .baseUrl(FLICKR_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        private val retrofitPixabay by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(logging).build()
            Retrofit.Builder()
                .baseUrl(PIXABAY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val api by lazy {
            retrofit.create(APIFlickr::class.java)
        }

        val apiPixabay by lazy {
            retrofitPixabay.create(APIPixabay::class.java)
        }
    }
}
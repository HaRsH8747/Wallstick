package com.wallstick

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.wallstick.api.RetrofitInstance
import com.wallstick.database.LatestPhoto
import com.wallstick.database.PhotoViewModel
import com.wallstick.databinding.ActivityCategoryBinding
import com.wallstick.models.flickr.FlickrResponse
import com.wallstick.models.pixabay.PixabayResponse
import com.wallstick.utils.AppPref
import com.wallstick.utils.ConnectionLiveData
import com.wallstick.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryActivity : AppCompatActivity() {

//    private lateinit var flickrResult: Call<FlickrResponse>
//    private lateinit var pixabayResult: Call<PixabayResponse>
//    private lateinit var mPhotoViewModel: PhotoViewModel
//    private var isDatabaseBusy = MutableLiveData<Boolean>(false)
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var snackbar: Snackbar
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var appPref: AppPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_Wallstick)
        setContentView(binding.root)
//        addNetworkListener()
        appPref = AppPref(this)
//        snackbar = Snackbar.make(binding.root,"You are offline, Please turn on the internet to get full experience",Snackbar.LENGTH_LONG)

//        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
//        mPhotoViewModel.readAllData.observe(this, Observer{ photo ->
//            if (photo.isEmpty()){
//                Log.d("CLEAR","Null or Empty")
//                fetchPixabayLatestPhotos()
//            }
//        })
    }

    private fun addNetworkListener(){
        connectionLiveData= ConnectionLiveData(application)
        connectionLiveData.observe(this) { isAvailable ->
            when (isAvailable) {
                true -> {
//                    snackbar.dismiss()
                    if (appPref.getBoolean(AppPref.IS_FIRST_OPEN,true)){
                        appPref.setBoolean(AppPref.IS_FIRST_OPEN,false)
                    }
                }
                false -> {
//                    snackbar.show()
                }
            }
        }
    }

//    @OptIn(DelicateCoroutinesApi::class)
//    private fun fetchPixabayLatestPhotos() {
//        val pixabayAPI = RetrofitInstance.apiPixabay
//        GlobalScope.launch {
////            pixabayResult = pixabayAPI.getPhotos()
//            pixabayAPI.getPhotos(order = "latest", page = 1, per_page = 20).enqueue(object : Callback<PixabayResponse> {
//                override fun onResponse(call: Call<PixabayResponse>, response: Response<PixabayResponse>) {
//                    if(response.body() != null){
//                        Utils.pixabayResponse = response.body()!!
//                        Log.d("CLEAR","Pixabay Response: ${Utils.pixabayResponse.total}")
//                        if (!isDatabaseBusy.value!!){
//                            isDatabaseBusy.value = true
//                            insertPixabayResponseToDatabase(
//                                isLatest = true,
//                                isTrending = false,
//                                isFavourite = false
//                            )
//                        }else{
//                            isDatabaseBusy.observe(this@CategoryActivity, Observer { isBusy ->
//                                if (!isBusy){
//                                    insertPixabayResponseToDatabase(
//                                        isLatest = true,
//                                        isTrending = false,
//                                        isFavourite = false
//                                    )
//                                }
//                            })
//                        }
//
//                    }
//                }
//                override fun onFailure(call: Call<PixabayResponse>, t: Throwable) {
//                    Log.d("CLEAR","MainActivity: ${t.message}")
//                    Toast.makeText(this@CategoryActivity,"Unable to fetch data!", Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//    }
//
//    @OptIn(DelicateCoroutinesApi::class)
//    fun fetchFlickrPhotos(){
//        val flickrAPI = RetrofitInstance.api
//        GlobalScope.launch {
//            flickrResult = flickrAPI.getRecentPhotos()
//            flickrResult.enqueue(object : Callback<FlickrResponse> {
//                override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
//                    if(response.body() != null){
//                        Utils.flickrResponse = response.body()!!
//                        Log.d("CLEAR","Flickr Response: ${Utils.flickrResponse.photos.total}")
//
//                        if (!isDatabaseBusy.value!!){
//                            isDatabaseBusy.value = true
//                            insertFlickrResponseToDatabase()
//                        }else{
//                            isDatabaseBusy.observe(this@CategoryActivity, Observer { isBusy ->
//                                if (!isBusy){
//                                    insertFlickrResponseToDatabase()
//                                }
//                            })
//                        }
//                    }
//                }
//                override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
//                    Log.d("CLEAR","MainActivity: ${t.message}")
//                    Toast.makeText(this@CategoryActivity,"Unable to fetch data!", Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//    }
//
//    private fun insertFlickrResponseToDatabase() {
//        for (item in Utils.flickrResponse.photos.photo){
//            val url = createURL(item.farm,item.id,item.secret)
//            Log.d("CLEAR","url $url")
//            val latestPhoto = LatestPhoto(
//                photoId = item.id.toLong(),
//                previewUrl = url,
//                originalUrl = url,
//                isLatest = true,
//                isTrending = false,
//                isFavourite = false)
//            mPhotoViewModel.addPhoto(latestPhoto)
//        }
//        isDatabaseBusy.postValue(false)
//    }
//
//    fun insertPixabayResponseToDatabase(isLatest: Boolean, isTrending: Boolean, isFavourite: Boolean){
//        for (item in Utils.pixabayResponse.hits){
//            val latestPhoto = LatestPhoto(
//                photoId = item.id.toLong(),
//                previewUrl = item.previewURL,
//                originalUrl = item.largeImageURL,
//                isLatest = isLatest,
//                isTrending = isTrending,
//                isFavourite = isFavourite)
//            mPhotoViewModel.addPhoto(latestPhoto)
//        }
//        isDatabaseBusy.postValue(false)
//    }
//
//    private fun createURL(farm: Int, id: String, secret: String): String{
//        return "https://farm$farm.staticflickr.com/$farm/${id}_$secret.jpg"
//    }
}
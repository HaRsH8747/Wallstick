package com.wallstick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.wallstick.api.RetrofitInstance
import com.wallstick.database.LatestPhoto
import com.wallstick.database.PhotoViewModel
import com.wallstick.models.flickr.FlickrResponse
import com.wallstick.models.pixabay.PixabayResponse
import com.wallstick.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryActivity : AppCompatActivity() {

    private lateinit var flickrResult: Call<FlickrResponse>
    private lateinit var pixabayResult: Call<PixabayResponse>
    private lateinit var mPhotoViewModel: PhotoViewModel
    private var isDatabaseBusy = MutableLiveData<Boolean>(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Wallstick)
        setContentView(R.layout.activity_category)

//        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
//        mPhotoViewModel.readAllData.observe(this, Observer{ photo ->
//            if (photo.isEmpty()){
//                Log.d("CLEAR","Null or Empty")
//                fetchPixabayLatestPhotos()
//            }
//        })
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
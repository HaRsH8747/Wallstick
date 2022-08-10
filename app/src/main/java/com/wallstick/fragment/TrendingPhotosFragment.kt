package com.wallstick.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.wallstick.adapters.PhotosAdapter
import com.wallstick.api.RetrofitInstance
import com.wallstick.database.LatestPhoto
import com.wallstick.database.PhotoViewModel
import com.wallstick.database.TrendingPhoto
import com.wallstick.databinding.FragmentTrendingPhotosBinding
import com.wallstick.models.pixabay.PixabayResponse
import com.wallstick.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrendingPhotosFragment : Fragment() {

    private lateinit var binding: FragmentTrendingPhotosBinding
    private lateinit var mPhotoViewModel: PhotoViewModel
    private var page = 1
    private var isFetched = false
    private var trendingPhotos = mutableListOf<LatestPhoto>()
    private lateinit var adapter: PhotosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTrendingPhotosBinding.inflate(layoutInflater)
        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        adapter = PhotosAdapter(requireContext(), mPhotoViewModel)
        binding.rvTrending.adapter = adapter
        binding.tvTrendingTitle.text = Utils.currentTrendingTag

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        mPhotoViewModel.readAllLatestPhotos.observe(viewLifecycleOwner, Observer { photos ->
//            mPhotoViewModel.readAllTrendingTag.observe(viewLifecycleOwner,Observer{ tags ->
//                val tagList = mutableListOf<String>()
//                for (tag in tags){
//                    tagList.add(tag.tagText)
//                }
//            })
            for (photo in photos) {
                val tagString = photo.tagList.split(",")
//                Log.d("CLEAR","tag is : ${Utils.currentTrendingTag}, photo tag: ${photo.tagList}")
                if (photo.tagList.contains(Utils.currentTrendingTag)) {
                    Log.d("CLEAR", "tag photo: ${photo}")
                    trendingPhotos.add(photo)
                }
//                for (i in tagString){
//                    if (tagList.contains(i.trim())){
//                        break
//                    }
//                }
                //                LatestPhotos.addAll(photos)
            }

            if (trendingPhotos.isEmpty()) {
//                Log.d("CLEAR","Null or Empty")
                fetchPixabayTrendingPhotos(page, 200)
                page++
            } else {
//                val currentPhotoList = photo.take(currentCounter)
//                Log.d("CLEAR","viewModel: ${photo.size}")
                adapter.setData(trendingPhotos, false)
            }
            mPhotoViewModel.readAllLatestPhotos.removeObservers(viewLifecycleOwner)
        })


        binding.rvTrending.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    binding.progressCircular.visibility = View.VISIBLE
//                    if (mPhotoViewModel.readAllData.value?.size!! > currentCounter){
//                        currentCounter += 20
//                        val currentPhotoList = mPhotoViewModel.readAllData.value?.take(currentCounter)!!
//                        adapter.setData(currentPhotoList)
//                        binding.progressCircular.visibility = View.GONE
//                    }else{
//                    }
//                        Log.d("CLEAR","nested page: ${latestPage} $latestPerPage")
                    fetchPixabayTrendingPhotos(page, 200)
                    page++
                    Log.d("CLEAR", "Tage Page: ${page}")
                }
            }
        })

        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchPixabayTrendingPhotos(page: Int, perPage: Int) {
        val pixabayAPI = RetrofitInstance.apiPixabay
        GlobalScope.launch {
//            pixabayResult = pixabayAPI.getPhotos()
            pixabayAPI.getSearchedPhotos(
                q = Utils.currentTrendingTag,
                page = page,
                per_page = perPage,
            ).enqueue(object :
                Callback<PixabayResponse> {
                override fun onResponse(
                    call: Call<PixabayResponse>,
                    response: Response<PixabayResponse>,
                ) {
                    if (response.body() != null) {
                        binding.progressCircular.visibility = View.GONE
                        Utils.pixabayResponse = response.body()!!
                        Log.d("CLEAR", "Pixabay Response: ${Utils.pixabayResponse.total}")
                        insertPixabayResponseToDatabase(
                            isLatest = false,
                            isTrending = true,
                            isFavourite = false
                        )
//                        if (!isDatabaseBusy.value!!){
//                            isDatabaseBusy.value = true
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
                    }
                }

                override fun onFailure(call: Call<PixabayResponse>, t: Throwable) {
                    Log.d("CLEAR", "MainActivity: ${t.message}")
                    Toast.makeText(requireContext(), "Unable to fetch data!", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    fun insertPixabayResponseToDatabase(
        isLatest: Boolean,
        isTrending: Boolean,
        isFavourite: Boolean,
    ) {
        for (item in Utils.pixabayResponse.hits) {
            val latestPhoto = LatestPhoto(
                photoId = item.id.toLong(),
                previewUrl = item.previewURL,
                originalUrl = item.largeImageURL,
                isFavourite = isFavourite,
                isLatest = isLatest,
                isTrending = isTrending,
                tagList = item.tags)
            trendingPhotos.add(latestPhoto)
            mPhotoViewModel.addLatestPhoto(latestPhoto)
        }
        adapter.setData(trendingPhotos, false)

//        isDatabaseBusy.postValue(false)
    }
}
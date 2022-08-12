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
import androidx.recyclerview.widget.RecyclerView
import com.wallstick.adapters.PhotosAdapter
import com.wallstick.api.RetrofitInstance
import com.wallstick.database.LatestPhoto
import com.wallstick.database.PhotoViewModel
import com.wallstick.databinding.FragmentLatestBinding
import com.wallstick.models.pixabay.PixabayResponse
import com.wallstick.utils.AppPref
import com.wallstick.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LatestFragment : Fragment() {

    private lateinit var binding: FragmentLatestBinding
    private lateinit var mPhotoViewModel: PhotoViewModel
    private var latestPage = 1
    private var latestPerPage = 20
    private lateinit var appPref: AppPref
    private var currentCounter = 20
    private var isFetched = false
    private var latestPhotos = mutableListOf<LatestPhoto>()
    companion object{
    }
    lateinit var adapter: PhotosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLatestBinding.inflate(layoutInflater,container,false)
        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        adapter = PhotosAdapter(requireContext(),mPhotoViewModel, false)
        binding.rvLatest.adapter = adapter
        appPref = AppPref(requireContext())
        latestPage = appPref.getInt(AppPref.LATEST_CURRENT_PAGE,1)
        latestPerPage = appPref.getInt(AppPref.LATEST_CURRENT_PER_PAGE,20)

        Log.d("CLEAR","onCreate page: ${latestPage} $latestPerPage")

        latestPhotos = mutableListOf<LatestPhoto>()
        mPhotoViewModel.readAllLatestPhotos.observe(viewLifecycleOwner, Observer{ photo ->
            if (!isFetched){
                latestPhotos.addAll(photo)
            }else{
                fetchPixabayLatestPhotos(latestPage, latestPerPage)
            }
            if (photo.isEmpty()){
//                Log.d("CLEAR","Null or Empty")
                fetchPixabayLatestPhotos(latestPage, latestPerPage)
            }else{
//                val currentPhotoList = photo.take(currentCounter)
//                Log.d("CLEAR","viewModel: ${photo.size}")
                adapter.setData(photo,false)
            }
            mPhotoViewModel.readAllLatestPhotos.removeObservers(viewLifecycleOwner)
        })

        binding.rvLatest.computeVerticalScrollOffset()

        binding.rvLatest.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)){
                    binding.progressCircular.visibility = View.VISIBLE
//                    if (mPhotoViewModel.readAllData.value?.size!! > currentCounter){
//                        currentCounter += 20
//                        val currentPhotoList = mPhotoViewModel.readAllData.value?.take(currentCounter)!!
//                        adapter.setData(currentPhotoList)
//                        binding.progressCircular.visibility = View.GONE
//                    }else{
//                    }
                        latestPage = appPref.getInt(AppPref.LATEST_CURRENT_PAGE,1) + 1
                        latestPerPage = appPref.getInt(AppPref.LATEST_CURRENT_PER_PAGE,20)
//                        Log.d("CLEAR","nested page: ${latestPage} $latestPerPage")
                        fetchPixabayLatestPhotos(latestPage,latestPerPage)
                }
            }
        })

//        binding.svNested.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            val svNested = v as NestedScrollView
//            if (scrollY == svNested.getChildAt(0).measuredHeight - svNested.measuredHeight){
//
//            }
//        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d("CLEAR","inside Resume")
//        if(Utils.isFavouriteUpdated){
//            Log.d("CLEAR","favourite changed")
//            adapter.notifyItemChanged(Utils.currentLatestPhotoIndex)
//            Utils.isFavouriteUpdated = false
//        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchPixabayLatestPhotos(page: Int, perPage: Int) {
        val pixabayAPI = RetrofitInstance.apiPixabay
        GlobalScope.launch {
//            pixabayResult = pixabayAPI.getPhotos()
            pixabayAPI.getPhotos(order = "latest", page = page, per_page = perPage).enqueue(object :
                Callback<PixabayResponse> {
                override fun onResponse(call: Call<PixabayResponse>, response: Response<PixabayResponse>) {
                    if(response.body() != null){
                        appPref.setInt(AppPref.LATEST_CURRENT_PAGE,page)
                        appPref.setInt(AppPref.LATEST_CURRENT_PER_PAGE,perPage)
                        binding.progressCircular.visibility = View.GONE
                        Utils.pixabayResponse = response.body()!!
                        Log.d("CLEAR","Pixabay Response: ${Utils.pixabayResponse.total}")
                        insertPixabayResponseToDatabase(
                            isLatest = true,
                            isTrending = false,
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
                    Log.d("CLEAR","MainActivity: ${t.message}")
                    Toast.makeText(requireContext(),"Unable to fetch data!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun insertPixabayResponseToDatabase(isLatest: Boolean, isTrending: Boolean, isFavourite: Boolean){
        for (item in Utils.pixabayResponse.hits){
            val latestPhoto = LatestPhoto(
                photoId = item.id.toLong(),
                previewUrl = item.webformatURL,
                originalUrl = item.largeImageURL,
                isFavourite = isFavourite,
                isLatest = isLatest,
                isTrending = isTrending,
                tagList = item.tags)
            latestPhotos.add(latestPhoto)
            mPhotoViewModel.addLatestPhoto(latestPhoto)
        }
        adapter.setData(latestPhotos, false)
//        isDatabaseBusy.postValue(false)
    }
}


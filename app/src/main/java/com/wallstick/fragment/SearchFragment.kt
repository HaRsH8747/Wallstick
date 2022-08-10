package com.wallstick.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.wallstick.adapters.PhotosAdapter
import com.wallstick.adapters.SearchedPhotosAdapter
import com.wallstick.api.RetrofitInstance
import com.wallstick.database.LatestPhoto
import com.wallstick.database.PhotoViewModel
import com.wallstick.databinding.FragmentSearchBinding
import com.wallstick.models.pixabay.PixabayResponse
import com.wallstick.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var mPhotoViewModel: PhotoViewModel
    private var page = 1
    private var perPage = 20
    private lateinit var adapter: PhotosAdapter
    private var searchList = mutableListOf<LatestPhoto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)

        WallpaperFragment.wallpaperBinding.etSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchList.clear()
                binding.progressCircular.visibility = View.GONE
                val keyword = WallpaperFragment.wallpaperBinding.etSearch.text.toString()
                searchPixabayPhotos(page = page, perPage = perPage, keyword = keyword)
                hideSoftKeyboard()
                return@OnEditorActionListener true
            }
            false
        })

        adapter = PhotosAdapter(requireContext(),mPhotoViewModel)
        binding.rvLatest.adapter = adapter

        binding.rvLatest.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)){
                    binding.progressCircular.visibility = View.VISIBLE
                    page++
                    val keyword = WallpaperFragment.wallpaperBinding.etSearch.text.toString()
                    searchPixabayPhotos(page = page, perPage = perPage, keyword = keyword)
                }
            }
        })

        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun searchPixabayPhotos(keyword: String,page: Int, perPage: Int){
        val pixabayAPI = RetrofitInstance.apiPixabay
        GlobalScope.launch {
//            pixabayResult = pixabayAPI.getPhotos()
            Log.d("CLEAR","fetch page: ${page} $perPage")
            pixabayAPI.getSearchedPhotos(page = page, per_page = perPage, q = keyword).enqueue(object :
                Callback<PixabayResponse> {
                override fun onResponse(call: Call<PixabayResponse>, response: Response<PixabayResponse>) {
                    if(response.body() != null){
                        binding.progressCircular.visibility = View.GONE
                        Utils.pixabayResponse = response.body()!!
                        Log.d("CLEAR","Pixabay Response: ${Utils.pixabayResponse.total}")
                        for (item in Utils.pixabayResponse.hits){
                            val latestPhoto = LatestPhoto(
                                photoId = item.id.toLong(),
                                previewUrl = item.previewURL,
                                originalUrl = item.largeImageURL,
                                isFavourite = false,
                                isLatest = false,
                                isTrending = true,
                                tagList = item.tags
                            )
                            searchList.add(latestPhoto)
                        }
                        adapter.setData(searchList,true)
//                        insertPixabayResponseToDatabase(
//                            isLatest = true,
//                            isTrending = false,
//                            isFavourite = false
//                        )
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

    fun hideSoftKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}
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
import com.wallstick.adapters.TrendingTagAdapter
import com.wallstick.api.RetrofitInstance
import com.wallstick.database.PhotoViewModel
import com.wallstick.database.TrendingTag
import com.wallstick.databinding.FragmentTrendingBinding
import com.wallstick.models.pixabay.Hit
import com.wallstick.models.pixabay.PixabayResponse
import com.wallstick.utils.AppPref
import com.wallstick.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrendingFragment : Fragment() {

    private lateinit var binding: FragmentTrendingBinding
    private lateinit var mPhotoViewModel: PhotoViewModel
    private var latestPage = 1
    private var latestPerPage = 20
    private lateinit var appPref: AppPref
    private var currentCounter = 20
    private var trendingTagList = mutableListOf<TrendingTag>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrendingBinding.inflate(layoutInflater,container,false)

        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        val adapter = TrendingTagAdapter(requireContext(),mPhotoViewModel)
        binding.rvTrendingTags.adapter = adapter

        mPhotoViewModel.readAllTrendingTag.observe(viewLifecycleOwner, Observer { tag ->
            if (tag.isEmpty()){
                fetchPixabayTrendingPhotos()
            }else{
                adapter.setData(tag)
            }
        })

        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchPixabayTrendingPhotos(){
        val pixabayAPI = RetrofitInstance.apiPixabay
        GlobalScope.launch {
            pixabayAPI.getPhotos(order = "latest", page = 1, per_page = 150).enqueue(object :
                Callback<PixabayResponse> {
                override fun onResponse(call: Call<PixabayResponse>, response: Response<PixabayResponse>) {
                    if(response.body() != null){
                        binding.progressCircular.visibility = View.GONE
                        Utils.pixabayResponse = response.body()!!
                        Log.d("CLEAR","Pixabay Response: ${Utils.pixabayResponse.total}")
                        insertPixabayTrendingTagToDatabase()
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

    private fun insertPixabayTrendingTagToDatabase() {
        val filteredTags = filterOutTags(Utils.pixabayResponse.hits as MutableList<Hit>)
        for (tag in filteredTags){
            val trendingTag = TrendingTag(
                0,
                tagText = tag.key,
                url = tag.value
            )
            mPhotoViewModel.addTrendingTag(trendingTag)
        }
    }

    private fun filterOutTags(hitList: MutableList<Hit>): Map<String,String> {
        val map = mutableMapOf<String,String>()
        for(item in hitList){
            val tags = item.tags.split(",")
            for (tag in tags){
               map[tag.trim()] = item.webformatURL
            }
        }
        Log.d("CLEAR","main map size: ${map.size}")

        val keys = map.keys.toList()
        val values = map.values.toList()
        val distinctMap = mutableMapOf<String,String>()
        val newKeys = map.keys.distinct()
        val newValues = map.values.distinct()
        for (value in newValues){
            distinctMap[keys[values.indexOf(value)]] = value
        }
//        for (key in keys){
//            if(distinctMap.values.contains(map.values.toList()[map.keys.toList().indexOf(key)])){
//                distinctMap[key] = map.values.toList()[map.keys.toList().lastIndexOf(key)]
//            }else{
//                distinctMap[key] = map.values.toList()[map.keys.toList().indexOf(key)]
//            }
//            Log.d("CLEAR","key: ${key}")
//        }
        return distinctMap
    }


}
package com.wallstick.fragment

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wallstick.R
import androidx.navigation.Navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManagerFactory
import com.wallstick.BuildConfig
import com.wallstick.databinding.FragmentHomeBinding
import com.wallstick.utils.AppPref
import com.wallstick.utils.ConnectionLiveData

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var appPref: AppPref
    var isFirstTime: Boolean = false
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var snackbar: Snackbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.network_off_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        addNetworkListener()
        appPref = AppPref(requireContext())
        binding.lsBtn.setOnClickListener { v ->
            if (appPref.getBoolean(AppPref.IS_FIRST_OPEN,true)){
                dialog.show()
            }else{
                findNavController(v).navigate(HomeFragmentDirections.actionHomeFragment2ToWallpaperFragment(0))
                appPref.setBoolean(AppPref.IS_FIRST_OPEN,false)
            }
        }
        binding.twBtn.setOnClickListener { v ->
            if (appPref.getBoolean(AppPref.IS_FIRST_OPEN,true)){
                dialog.show()
            }else{
                findNavController(v).navigate(HomeFragmentDirections.actionHomeFragment2ToWallpaperFragment(1))
                appPref.setBoolean(AppPref.IS_FIRST_OPEN,false)
            }
        }
        binding.favBtn.setOnClickListener { v ->
            if (appPref.getBoolean(AppPref.IS_FIRST_OPEN,true)){
                dialog.show()
            }else{
                findNavController(v).navigate(HomeFragmentDirections.actionHomeFragment2ToWallpaperFragment(2))
                appPref.setBoolean(AppPref.IS_FIRST_OPEN,false)
            }
        }
        binding.rsBtn.setOnClickListener { v -> rateApplication() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snackbar = Snackbar.make(binding.root,"You are offline, Please turn on the internet to get full experience",Snackbar.LENGTH_INDEFINITE)
    }

    private fun rateApplication(){
        val manager = ReviewManagerFactory.create(requireContext())
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("CLEAR","request Successful")
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener {
                    Log.d("CLEAR","launch Successful")
                }
            } else {
                goToPlayStore()
            }
        }
        request.addOnFailureListener {
            goToPlayStore()
        }
    }

    private fun goToPlayStore() {
        val uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)))
        }
    }

    private fun addNetworkListener(){
        connectionLiveData= ConnectionLiveData(requireActivity().application)
        connectionLiveData.observe(viewLifecycleOwner) { isAvailable ->
            when (isAvailable) {
                true -> {
                    snackbar.dismiss()
                    if (appPref.getBoolean(AppPref.IS_FIRST_OPEN,true)){
                        appPref.setBoolean(AppPref.IS_FIRST_OPEN,false)
                    }
                }
                false -> {
                    snackbar.show()
                }
            }
        }
    }
}
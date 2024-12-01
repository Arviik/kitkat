package com.example.kitkat.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.kitkat.R
import com.example.kitkat.databinding.FragmentHomeBinding
import com.example.kitkat.ui.home.video.VideoPagerAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: VideoPagerAdapter

    private val videos = listOf(
        "https://kitkatstorage.blob.core.windows.net/videos/RPReplay_Final1714571614 (2).mp4?sp=racwdli&st=2024-11-09T15:48:36Z&se=2025-03-30T22:48:36Z&sv=2022-11-02&sr=c&sig=FmEgXVr0WKXGjHjqyDzrWHrweHSX0MV2uS0kIlR9mCo%3D",
        "https://kitkatstorage.blob.core.windows.net/videos/RPReplay_Final1714571614 (2).mp4?sp=racwdli&st=2024-11-09T15:48:36Z&se=2025-03-30T22:48:36Z&sv=2022-11-02&sr=c&sig=FmEgXVr0WKXGjHjqyDzrWHrweHSX0MV2uS0kIlR9mCo%3D",
        "https://kitkatstorage.blob.core.windows.net/videos/RPReplay_Final1714571614 (2).mp4?sp=racwdli&st=2024-11-09T15:48:36Z&se=2025-03-30T22:48:36Z&sv=2022-11-02&sr=c&sig=FmEgXVr0WKXGjHjqyDzrWHrweHSX0MV2uS0kIlR9mCo%3D"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = VideoPagerAdapter(videos, parentFragmentManager)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.updateActivePosition(position)
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        Log.d("HomeFragment", "onPause called: Releasing active player")
        if (::adapter.isInitialized) {
            adapter.releaseAllPlayers()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("HomeFragment", "onDestroyView: Releasing all players")
        if (::adapter.isInitialized) {
            adapter.releaseAllPlayers()
        }
        _binding = null
    }
}

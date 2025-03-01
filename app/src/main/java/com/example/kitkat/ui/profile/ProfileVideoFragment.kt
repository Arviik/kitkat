package com.example.kitkat.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.kitkat.R
import com.example.kitkat.network.dto.VideoWithAuthor
import com.example.kitkat.ui.home.video.VideoPagerAdapter

class ProfileVideoFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: VideoPagerAdapter
    private var videos: List<VideoWithAuthor> = emptyList()
    private var startPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videos = it.getSerializable("videos") as? List<VideoWithAuthor> ?: emptyList()
            startPosition = it.getInt("position", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile_video, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        adapter = VideoPagerAdapter(videos, childFragmentManager)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(startPosition, false) // Définit la vidéo initiale
        return view
    }
}

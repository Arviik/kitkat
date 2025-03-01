package com.example.kitkat.ui.profile.video

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.kitkat.R
import com.example.kitkat.api.models.dataclass.VideoWithAuthor
import com.example.kitkat.ui.home.video.VideoPagerAdapter

class VideoPagerFragment : Fragment() {

    private lateinit var videoPagerAdapter: VideoPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("VideoPagerFragment", "onCreate called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("VideoPagerFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_video_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Récupérer les vidéos et la position initiale depuis les arguments
        val videos = arguments?.getParcelableArrayList<VideoWithAuthor>("videos") ?: emptyList()
        val initialPosition = arguments?.getInt("videoPosition") ?: 0

        Log.d("VideoPagerFragment", "Vidéos reçues : ${videos.size}")
        Log.d("VideoPagerFragment", "Position initiale : $initialPosition")

        // Configurer le ViewPager
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        videoPagerAdapter = VideoPagerAdapter(videos, childFragmentManager)
        viewPager.adapter = videoPagerAdapter
        viewPager.setCurrentItem(initialPosition, false)
    }

}


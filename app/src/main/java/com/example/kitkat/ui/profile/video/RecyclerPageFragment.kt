package com.example.kitkat.ui.profile.video

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R
import com.example.kitkat.network.dto.VideoWithAuthor
import androidx.navigation.fragment.findNavController

class RecyclerPageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var videoAdapter: VideoAdapter
    private var videos: List<VideoWithAuthor> = emptyList()
    private var items: List<VideoWithAuthor> = listOf()

    companion object {
        fun newInstance(items: List<VideoWithAuthor>): RecyclerPageFragment {
            val fragment = RecyclerPageFragment()
            val args = Bundle().apply {
                putParcelableArrayList("items", ArrayList(items)) // Utiliser une liste Parcelable
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            items = it.getParcelableArrayList("items") ?: emptyList()
            Log.d("RecyclerPageFragment", "Items reçus : $items")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_recycler_page, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewGrid)
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        Log.d("RecyclerPageFragment", "Items envoyés à l'adaptateur : $items")

        videoAdapter = VideoAdapter(items) { videoWithAuthor, position ->
            val bundle = Bundle().apply {
                putInt("videoPosition", position) // Position initiale de la vidéo
                putParcelableArrayList("videos", ArrayList(items)) // Liste des vidéos
            }
            findNavController().navigate(R.id.action_profile_to_profileVideos, bundle)
        }


        recyclerView.adapter = videoAdapter

        return view
    }



    fun updateVideos(newVideos: List<VideoWithAuthor>) {
        videos = newVideos
        videoAdapter.notifyDataSetChanged()
    }
}
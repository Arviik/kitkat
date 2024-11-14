package com.example.kitkat.ui.profile.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R
import com.example.kitkat.model.VideoItem

class RecyclerPageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var videoAdapter: VideoAdapter

    companion object {
        fun newInstance(items: List<VideoItem>): RecyclerPageFragment {
            val fragment = RecyclerPageFragment()
            val args = Bundle()
            args.putParcelableArrayList("items", ArrayList(items))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_recycler_page, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewGrid)
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        val items = arguments?.getParcelableArrayList<VideoItem>("items") ?: listOf()

        videoAdapter = VideoAdapter(items)
        recyclerView.adapter = videoAdapter

        return view
    }
}
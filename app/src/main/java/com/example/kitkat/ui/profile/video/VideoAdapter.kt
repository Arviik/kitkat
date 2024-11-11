package com.example.kitkat.ui.profile.video

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kitkat.R
import com.example.kitkat.model.VideoItem

class VideoAdapter(private val items: List<VideoItem>) : RecyclerView.Adapter<VideoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_cell, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = items[position]

        Glide.with(holder.itemView.context)
            .load(item.thumbnailUrl)
            .centerCrop()
            .into(holder.thumbnailImageView)

        holder.viewsTextView.text = item.views
    }

    override fun getItemCount() = items.size
}

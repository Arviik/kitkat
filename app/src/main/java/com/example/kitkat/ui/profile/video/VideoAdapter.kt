package com.example.kitkat.ui.profile.video

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kitkat.R
import com.example.kitkat.network.dto.VideoWithAuthor

class VideoAdapter(
    private val items: List<VideoWithAuthor>,
    private val onVideoClicked: (VideoWithAuthor, Int) -> Unit // Gestion des clics
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnailImageView: ImageView = view.findViewById(R.id.imageViewThumbnail)
        val viewsTextView: TextView = view.findViewById(R.id.textViewViews)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_cell, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoWithAuthor = items[position]
        val video = videoWithAuthor.first
        Log.d("VideoAdapter", "URL vidéo : ${video.videoUrl}")
        Log.d("VideoAdapter", "URL miniature : ${video.thumbnailUrl}")
        Log.d("VideoAdapter", "Position cliquée : $position, Vidéo : ${videoWithAuthor.first.title}")

        // Charger la miniature
        Glide.with(holder.itemView.context)
            .load(video.thumbnailUrl)
            .into(holder.thumbnailImageView)

        // Afficher le nombre de vues
        holder.viewsTextView.text = "${video.viewCount} vues"

        // Gestion des clics
        holder.itemView.setOnClickListener {
            onVideoClicked(videoWithAuthor, position)
        }
    }

    override fun getItemCount(): Int = items.size
}

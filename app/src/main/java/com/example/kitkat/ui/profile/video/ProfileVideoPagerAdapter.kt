package com.example.kitkat.ui.profile.video

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R
import com.example.kitkat.network.dto.VideoWithAuthor

class ProfileVideoPagerAdapter(
    private var videos: List<VideoWithAuthor>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<ProfileVideoPagerAdapter.VideoViewHolder>() {

    private var activePosition: Int = RecyclerView.NO_POSITION
    private var activePlayer: ExoPlayer? = null

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerView: PlayerView = itemView.findViewById(R.id.playerView)
        val username: TextView = itemView.findViewById(R.id.username)
        val videoDescription: TextView = itemView.findViewById(R.id.videoDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_profile_video_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoWithAuthor = videos[position]

        // Configure Player
        holder.playerView.player = ExoPlayer.Builder(holder.itemView.context).build().apply {
            setMediaItem(MediaItem.fromUri(videoWithAuthor.first.videoUrl))
            prepare()
            playWhenReady = position == activePosition
        }

        // Configure user details
        holder.username.text = videoWithAuthor.second.name
        holder.videoDescription.text = videoWithAuthor.first.title
    }

    override fun getItemCount() = videos.size
}

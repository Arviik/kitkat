package com.example.kitkat.ui.home.video

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.FragmentManager
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R
import com.example.kitkat.ui.comment.CommentFragment

class VideoPagerAdapter(
    private val videos: List<String>,
    private val parentFragmentManager: FragmentManager
) : RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder>() {

    private var activePosition: Int = RecyclerView.NO_POSITION
    private var activePlayer: ExoPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerView: PlayerView = itemView.findViewById(R.id.playerView)
        val progressBar: SeekBar = itemView.findViewById(R.id.progressBar)
        var exoPlayer: ExoPlayer? = null
        val commentButton: View = itemView.findViewById(R.id.commentButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_home_video_item, parent, false)
        return VideoViewHolder(view)
    }

    fun releaseAllPlayers() {
        Log.d("VideoPagerAdapter", "Releasing all active players")
        activePlayer?.let {
            it.stop()
            it.release()
            activePlayer = null
        }
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoUrl = videos[position]

        if (activePosition != position) {
            activePlayer?.pause()
        }

        holder.exoPlayer?.release()
        holder.exoPlayer = ExoPlayer.Builder(holder.itemView.context).build()
        holder.playerView.player = holder.exoPlayer

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(videoUrl))
            .build()

        holder.exoPlayer?.setMediaItem(mediaItem)
        holder.exoPlayer?.prepare()
        holder.exoPlayer?.playWhenReady = position == activePosition
        holder.exoPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_ONE // Répéter la vidéo

        activePlayer = holder.exoPlayer

        holder.exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == ExoPlayer.STATE_READY) {
                    handler.post(updateProgressBar(holder))
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) {
                    handler.post(updateProgressBar(holder))
                } else {
                    handler.removeCallbacksAndMessages(null)
                }
            }
        })

        holder.progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = holder.exoPlayer?.duration ?: 0
                    val seekPosition = duration * progress / 100
                    holder.exoPlayer?.seekTo(seekPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        holder.playerView.setOnClickListener {
            val isPlaying = holder.exoPlayer?.isPlaying ?: false
            holder.exoPlayer?.playWhenReady = !isPlaying
        }

        holder.commentButton.setOnClickListener {
            val comment = CommentFragment()
            comment.show(parentFragmentManager, "CommentBottomSheet")
        }
    }

    override fun getItemCount(): Int = videos.size

    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        holder.exoPlayer?.release()
        holder.exoPlayer = null
    }

    fun updateActivePosition(newPosition: Int) {
        val previousPosition = activePosition
        activePosition = newPosition
        Log.d("VideoPagerAdapter", "Updating position: previous=$previousPosition, new=$newPosition")
        if (previousPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousPosition)
        }
        notifyItemChanged(newPosition)
    }

    private fun updateProgressBar(holder: VideoViewHolder): Runnable = Runnable {
        val player = holder.exoPlayer ?: return@Runnable
        val duration = player.duration
        val currentPosition = player.currentPosition

        if (duration > 0) {
            val progress = (currentPosition * 100 / duration).toInt()
            holder.progressBar.progress = progress
        }

        if (player.isPlaying) {
            handler.postDelayed(updateProgressBar(holder), 1000)
        }
    }
}

package com.example.kitkat.ui.home.video
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kitkat.R
import com.example.kitkat.api.models.dataclass.UserWithoutPasswordDTO
import com.example.kitkat.api.models.dataclass.VideoWithAuthor
import com.example.kitkat.ui.comment.CommentFragment
import com.example.kitkat.ui.profile.ProfileFragment

class VideoPagerAdapter(
    private var videos: List<VideoWithAuthor>,
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
        val profileImage: View= itemView.findViewById(R.id.imageViewProfile)
        val likeButton: View = itemView.findViewById(R.id.likeButton)
        val shareButton: View = itemView.findViewById(R.id.shareButton)
        val username: TextView = itemView.findViewById(R.id.username)
        val videoDescription: TextView = itemView.findViewById(R.id.videoDescription)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val commentCount: TextView = itemView.findViewById(R.id.commentCount)
        val shareCount: TextView = itemView.findViewById(R.id.shareCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_home_video_item, parent, false)
        return VideoViewHolder(view)
    }
    fun updateVideos(newVideos: List<VideoWithAuthor>) {
        videos = newVideos
        notifyDataSetChanged()
    }
    fun releaseAllPlayers() {
        Log.d("VideoPagerAdapter", "Releasing all active players")
        activePlayer?.let {
            it.stop()
            it.release()
            activePlayer = null
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {

        val videoWithAuthor = videos[position]
        if (videoWithAuthor.first == null || videoWithAuthor.first.videoUrl.isNullOrEmpty()) {
            Log.e("VideoPagerAdapter", "Video or Video URL is null at position $position")
            return
        }
        Log.d("VideoPagerAdapter", "URL vidéo : $videoWithAuthor.first.videoUrl")
        Log.d("VideoPagerAdapter", "URL miniature : ${videoWithAuthor.first.thumbnailUrl}")

        val videoUrl = videoWithAuthor.first.videoUrl
        val profileImageUrl = videoWithAuthor.second.profilePictureUrl
        val author = videoWithAuthor.second

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
        Glide.with(holder.itemView.context)
            .load(profileImageUrl)
            .placeholder(R.drawable.ic_profil_black_font_grey_24dp)
            .error(R.drawable.ic_profil_black_font_grey_24dp)
            .circleCrop()
            .into(holder.profileImage as ImageView)
        holder.username.text = author.name
        holder.videoDescription.text = videoWithAuthor.first.title

        holder.likeCount.text = videoWithAuthor.first.likeCount.toString()
        holder.commentCount.text = videoWithAuthor.first.commentCount.toString()
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
            val comment = CommentFragment().apply {
                arguments = Bundle().apply {
                    videoWithAuthor.first.id?.let { it1 -> putInt("videoId", it1) } // 🔹 Passe l'ID de la vidéo
                }
            }
            comment.show(parentFragmentManager, "CommentBottomSheet")
        }
        holder.likeButton.setOnClickListener {
            // Ajouter ici l'interaction avec l'API pour gérer les likes
            Log.d("VideoPagerAdapter", "Like clicked for video ${videoWithAuthor.first.id}")
        }

        holder.shareButton.setOnClickListener {
            // Ajouter ici l'interaction pour le partage
            Log.d("VideoPagerAdapter", "Share clicked for video ${videoWithAuthor.first.id}")
        }
        holder.profileImage.setOnClickListener {
            val user = author

            val bundle = Bundle().apply {
                putSerializable("user", user)
                putInt("videoPosition", position) // position de la vidéo
            }

            val navController = Navigation.findNavController(holder.itemView)
            navController.navigate(R.id.navigation_profile, bundle)
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

package com.example.kitkat.ui.profile.video

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.kitkat.api.models.dataclass.UserWithoutPasswordDTO
import com.example.kitkat.api.models.dataclass.VideoDTO
import com.example.kitkat.api.models.dataclass.VideoWithAuthor

class VideoPlayerFragment : Fragment() {

    companion object {
        fun newInstance(videoWithAuthor: VideoWithAuthor): VideoPlayerFragment {
            val fragment = VideoPlayerFragment()
            val args = Bundle().apply {
                putSerializable("video", videoWithAuthor.first) // VideoDTO
                putSerializable("author", videoWithAuthor.second) // UserWithoutPasswordDTO
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val video = arguments?.getSerializable("video") as? VideoDTO
        val author = arguments?.getSerializable("author") as? UserWithoutPasswordDTO

        Log.d("VideoPlayerFragment", "Vidéo : $video")
        Log.d("VideoPlayerFragment", "Auteur : $author")

        // Configurez ici votre lecteur vidéo avec `video.videoUrl`
        // Configurez les informations d'utilisateur avec `author`
    }
}

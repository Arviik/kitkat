package com.example.kitkat.ui.camera

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kitkat.R

class VideoPreviewFragment : Fragment() {

    private lateinit var videoView: VideoView
    private lateinit var addTextButton: ImageButton
    private lateinit var nextButton: Button
    private lateinit var dynamicText: TextView
    private var videoUri: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_video_preview, container, false)

        videoView = view.findViewById(R.id.videoView)
        addTextButton = view.findViewById(R.id.addTextButton)
        nextButton = view.findViewById(R.id.nextButton)
        dynamicText = view.findViewById(R.id.dynamicText)

        videoUri = arguments?.getString("videoUri")

        if (videoUri.isNullOrEmpty()) {
            Log.e("VideoPreviewFragment", "URI vidéo est null ou vide")
            Toast.makeText(requireContext(), "Erreur : URI vidéo introuvable", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("VideoPreviewFragment", "URI vidéo reçue : $videoUri")
            videoView.setVideoURI(Uri.parse(videoUri))
            videoView.start()
            videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true // Boucle la vidéo
            }
        }

        addTextButton.setOnClickListener { showTextInputDialog() }
        nextButton.setOnClickListener { navigateToDescription() }

        return view
    }

    private fun showTextInputDialog() {
        val input = EditText(requireContext())
        input.hint = "Ajouter du texte"
        AlertDialog.Builder(requireContext())
            .setTitle("Ajouter un texte")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val text = input.text.toString()
                if (text.isNotEmpty()) {
                    dynamicText.text = text
                    dynamicText.visibility = View.VISIBLE
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun navigateToDescription() {
        if (videoUri.isNullOrEmpty()) {
            Log.e("VideoPreviewFragment", "Impossible de naviguer : URI vidéo est null")
            Toast.makeText(requireContext(), "Erreur : URI vidéo introuvable", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("VideoPreviewFragment", "URI vidéo envoyée : $videoUri")

        val bundle = Bundle().apply {
            putString("videoUri", videoUri)
        }
        findNavController().navigate(R.id.action_videoPreview_to_videoDescription, bundle)
    }
}

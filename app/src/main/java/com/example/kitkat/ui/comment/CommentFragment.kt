package com.example.kitkat.ui.comment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kitkat.R
import com.example.kitkat.network.ApiClient
import com.example.kitkat.network.dto.CommentDTO
import com.example.kitkat.network.services.CommentService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentFragment : BottomSheetDialogFragment() {

    private lateinit var commentService: CommentService
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var postButton: ImageButton
    private lateinit var adapter: CommentAdapter
    private var comments: MutableList<CommentDTO> = mutableListOf()
    private var videoId: Int = 0 // Id de la vidéo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_comment, container, false)

        // 🔹 Vérifie si l'ID de la vidéo est bien reçu
        videoId = arguments?.getInt("videoId") ?: 0
        Log.d("CommentFragment", "videoId reçu: $videoId")

        commentService = ApiClient.retrofit.create(CommentService::class.java)

        recyclerView = view.findViewById(R.id.commentRecyclerView)
        commentInput = view.findViewById(R.id.commentInput)
        postButton = view.findViewById(R.id.postButton)

        val profileImageUrl = "https://www.pngplay.com/wp-content/uploads/12/User-Avatar-Profile-PNG-Photos.png"
        Glide.with(this)
            .load(profileImageUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_profil_black_font_grey_24dp)
            .into(view.findViewById(R.id.profileImage))

        // 🔹 Initialisation de l'adaptateur
        comments = mutableListOf()
        adapter = CommentAdapter(comments)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // 🔹 Charger les commentaires
        fetchCommentsFromApi()

        postButton?.setOnClickListener {
            val comment = commentInput.text.toString()
            if (comment.isNotEmpty()) {
                postComment(comment)
            } else {
                Toast.makeText(context, "Veuillez saisir un commentaire", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }



    private fun loadFakeComments() {
        val fakeComments = listOf(
            CommentDTO(
                id = 1,
                authorId = 101,
                videoId = videoId,
                text = "Incroyable vidéo ! 🔥",
                createdAt = "2025-02-28T10:15:30Z",
                likesCount = 12,
                authorName = "Shokzuu"
            ),
            CommentDTO(
                id = 2,
                authorId = 102,
                videoId = videoId,
                text = "Ça c’est du contenu de qualité 😍",
                createdAt = "2025-02-28T10:20:45Z",
                likesCount = 8,
                authorName = "Purpleladétaille"
            ),
            CommentDTO(
                id = 3,
                authorId = 103,
                videoId = videoId,
                text = "Mdrrr la transition elle est propre 😂",
                createdAt = "2025-02-28T10:30:10Z",
                likesCount = 5,
                authorName = "Ugo"
            ),
            CommentDTO(
                id = 4,
                authorId = 104,
                videoId = videoId,
                text = "Tu t’améliores de ouf ! Continue comme ça 💪",
                createdAt = "2025-02-28T10:45:50Z",
                likesCount = 20,
                authorName = "Killian"
            ),
            CommentDTO(
                id = 5,
                authorId = 105,
                videoId = videoId,
                text = "On attend la suite 👀🔥",
                createdAt = "2025-02-28T11:00:00Z",
                likesCount = 14,
                authorName = "Talal"
            )
        )

        comments.clear()
        comments.addAll(fakeComments)
        adapter.notifyDataSetChanged()
    }

    private fun fetchCommentsFromApi() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = commentService.getCommentsByVideo(videoId).execute()
                if (response.isSuccessful) {
                    val commentData = response.body()
                    if (commentData != null) {
                        comments.clear()
                        comments.addAll(commentData)
                        withContext(Dispatchers.Main) {
                            adapter = CommentAdapter(comments)
                            recyclerView.layoutManager = LinearLayoutManager(context)
                            recyclerView.adapter = adapter
                        }
                    }
                } else {
                    Log.e("CommentFragment", "Erreur API: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CommentFragment", "Erreur réseau: ${e.message}", e)
            }
        }
    }

    private fun postComment(commentText: String) {
        Log.d("CommentFragment", "Envoi du commentaire pour videoId: $videoId")

        if (!::recyclerView.isInitialized) {
            Log.e("CommentFragment", "RecyclerView n'est pas initialisé !")
            return
        }

        val commentDTO = CommentDTO(
            id = 0,
            authorId = 1, // Remplace par l'ID de l'utilisateur actuel
            videoId = videoId,
            text = commentText,
            createdAt = "2025-02-28T12:00:00Z",
            likesCount = 0,
            authorName = "Moi"
        )

        commentService.postComment(commentDTO).enqueue(object : Callback<CommentDTO> {
            override fun onResponse(call: Call<CommentDTO>, response: Response<CommentDTO>) {
                if (response.isSuccessful) {
                    response.body()?.let { comment ->
                        Log.d("CommentFragment", "Commentaire ajouté avec succès: $comment")

                        // 🔹 Vérifions si le commentaire a bien des données
                        Log.d("CommentFragment", "ID: ${comment.id}, Texte: ${comment.text}, Auteur: ${comment.authorName}")

                        comments.add(0, comment)
                        adapter.notifyItemInserted(0)
                        recyclerView.scrollToPosition(0)
                    } ?: Log.e("CommentFragment", "Réponse vide de l'API")

                    commentInput.text.clear()
                } else {
                    Log.e("CommentFragment", "Échec de l'ajout du commentaire: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CommentDTO>, t: Throwable) {
                Log.e("CommentFragment", "Erreur réseau: ${t.message}", t)
            }
        })
    }

}

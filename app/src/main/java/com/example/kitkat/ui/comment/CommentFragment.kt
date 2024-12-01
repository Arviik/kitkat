package com.example.kitkat.ui.comment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kitkat.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CommentFragment() : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comment, container, false)

        val commentInput: EditText = view.findViewById(R.id.commentInput)
        val postButton: ImageButton? = view.findViewById<ImageButton>(R.id.postButton)
        val recyclerView: RecyclerView = view.findViewById(R.id.commentRecyclerView)

        val profileImageUrl = "https://www.pngplay.com/wp-content/uploads/12/User-Avatar-Profile-PNG-Photos.png"
        Glide.with(this)
            .load(profileImageUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_profil_black_font_grey_24dp)
            .into(view.findViewById(R.id.profileImage))


        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CommentAdapter(getSampleComments())

        postButton?.setOnClickListener {
            val comment = commentInput.text.toString()
            if (comment.isNotEmpty()) {

                Toast.makeText(context, "Commentaire posté : $comment", Toast.LENGTH_SHORT).show()
                commentInput.text.clear()
            } else {
                Toast.makeText(context, "Veuillez saisir un commentaire", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }



    // les faux commentaire
    private fun getSampleComments(): List<String> {
        return listOf("Super vidéo !", "C'est génial 😍", "J'adore ce contenu ❤️","C pour ca Shokzuu a pas de meuf","Ugo fini l'api")
    }
}
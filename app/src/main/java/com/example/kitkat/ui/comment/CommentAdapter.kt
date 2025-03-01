package com.example.kitkat.ui.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R
import com.example.kitkat.network.dto.CommentDTO

class CommentAdapter(private val comments: List<CommentDTO>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val authorName: TextView = itemView.findViewById(R.id.commentUsername)
        val createdAt: TextView = itemView.findViewById(R.id.commentDate)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.commentText.text = comment.text
        holder.authorName.text = comment.authorName
        holder.createdAt.text = comment.createdAt
        holder.likeCount.text = comment.likesCount.toString()
    }

    override fun getItemCount(): Int = comments.size
}

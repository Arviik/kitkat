package com.example.kitkat.ui.profile.video

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R

class VideoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val thumbnailImageView: ImageView = itemView.findViewById(R.id.imageViewThumbnail)
    val viewsTextView: TextView = itemView.findViewById(R.id.textViewViews)
}
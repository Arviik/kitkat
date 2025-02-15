package com.example.kitkat.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R
import com.example.kitkat.model.ConversationItem


class MessageConversationAdapter(private val conversations: List<ConversationItem>):
    RecyclerView.Adapter<MessageConversationAdapter.MessageConversationViewHolder>() {

    class MessageConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lastMessage: TextView = itemView.findViewById(R.id.messageLastMessageText)
        val username: TextView = itemView.findViewById(R.id.messageUsernameText)
        val timeSince: TextView = itemView.findViewById(R.id.messageTimeSinceText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message_conversation, parent, false)
        return MessageConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageConversationViewHolder, position: Int) {
        holder.lastMessage.text = conversations[position].lastMessage
        holder.username.text = conversations[position].username
        holder.timeSince.text = conversations[position].timeLasted
    }

    override fun getItemCount(): Int = conversations.size
}
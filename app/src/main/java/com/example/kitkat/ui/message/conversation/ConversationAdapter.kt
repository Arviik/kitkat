package com.example.kitkat.ui.message.conversation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R
import com.example.kitkat.model.ConversationItem
import com.example.kitkat.model.MessageItem
import com.example.kitkat.model.MessageSender

class ConversationAdapter(var messages: List<MessageItem>) :
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.conversationMessage)
    }

    companion object {
        private const val TYPE_ME = 0
        private const val TYPE_THEM = 1
        private const val TYPE_INFO = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].sender) {
            MessageSender.ME -> TYPE_ME
            MessageSender.THEM -> TYPE_THEM
            MessageSender.INFO -> TYPE_INFO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = when (viewType) {
            TYPE_ME -> {
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_conversation_message_mine, parent, false
                )
            }

            TYPE_THEM -> {
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_conversation_message_theirs, parent, false
                )
            }

            TYPE_INFO -> {
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_conversation_message_misc, parent, false
                )
            }

            else -> {
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_conversation_message_misc, parent, false
                )
            }
        }
        return ConversationViewHolder(view)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.message.text = messages[position].message
    }

    fun updateItems(newItems: List<MessageItem>) {
        messages = newItems
        notifyDataSetChanged() // Rafraîchit la RecyclerView avec les nouvelles données
    }
}
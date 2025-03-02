package com.example.kitkat.ui.message.conversation

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R
import com.example.kitkat.databinding.ActivityConversationBinding
import com.example.kitkat.model.MessageItem
import com.example.kitkat.model.MessageSender
import com.example.kitkat.network.dto.Message
import com.example.kitkat.repositories.MessageRepository
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.log

class ConversationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConversationBinding



    private val messageRepository = MessageRepository()
    private lateinit var adapter: ConversationAdapter
    private lateinit var messageInput: EditText

    private var conversationId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.conversation)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = binding.conversationRecyclerView

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        adapter = ConversationAdapter(emptyList())
        recyclerView.adapter = adapter

        val backButton: TextView = binding.conversationBackButton
        backButton.setOnClickListener {
            finish()
        }

        val senderTextView: TextView = binding.tabMessageSender
        senderTextView.text = intent.getStringExtra("USERNAME") ?: "undefined"
        conversationId = intent.getStringExtra("CONVERSATION_ID")?.toInt() ?: 0
        messageInput = binding.messageInput

        val sendButton = binding.sendMessageButton
        sendButton.setOnClickListener {
            sendMessage()
        }

        loadConversationMessages()
    }

    private fun getSampleConversation(): List<MessageItem> {
        return listOf(
            MessageItem("Today", MessageSender.INFO),
        )
    }

    private fun loadConversationMessages() {
        messageRepository.getMessagesByConversation(4,
            { conversations ->
                val items = conversations.map {
                    val sender: MessageSender = when (it.senderId) {
                        2 -> MessageSender.ME;
                        else -> {
                            if (it.isSystemMessage) {
                                MessageSender.INFO
                            } else {
                                MessageSender.THEM
                            }
                        }
                    }
                    MessageItem(
                        message = it.content,
                        sender = sender
                    )
                }
                Log.d("MESSAGE", "loadConversationMessages: $items")


                val dateList = listOf(MessageItem("TODAY", MessageSender.INFO))
                updateRecyclerView(dateList.plus(items))
            },
            { error ->
                Log.e("ERROR", "loadUserConversations: ${error.message}")
                Toast.makeText(applicationContext, "Erreur : ${error.message}", Toast.LENGTH_SHORT)
                    .show()
                updateRecyclerView(emptyList())
            }
        )
    }

    private fun sendMessage() {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val message: Message = Message(
            1,
            1,
            messageInput.text.toString(),
            conversationId = conversationId,
            createdAt = sdf.format(currentDate),
            isSystemMessage = false
        )
        messageRepository.sendMessageToConversation(message,
            onSuccess = {
                messageInput.setText("")
                loadConversationMessages()
            },
            onError = { error ->
                Log.e("ERROR", "loadUserConversations: ${error.message}")
                Toast.makeText(applicationContext, "Erreur : ${error.message}", Toast.LENGTH_SHORT)
                    .show()
                updateRecyclerView(emptyList())
            })
    }

    private fun updateRecyclerView(items: List<MessageItem>) {
        adapter.updateItems(items)
    }
}



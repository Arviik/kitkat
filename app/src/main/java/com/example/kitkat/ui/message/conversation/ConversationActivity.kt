package com.example.kitkat.ui.message.conversation

import ChatWebSocketListener
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R
import com.example.kitkat.app_utils.SHARED_PREF_KEY
import com.example.kitkat.databinding.ActivityConversationBinding
import com.example.kitkat.model.MessageItem
import com.example.kitkat.model.MessageSender
import com.example.kitkat.network.dto.Message
import com.example.kitkat.repositories.MessageRepository
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.log

class ConversationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConversationBinding

    private lateinit var sharedPref: SharedPreferences;

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

        sharedPref = applicationContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)

        val recyclerView: RecyclerView = binding.conversationRecyclerView

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        adapter = ConversationAdapter(emptyList())
        recyclerView.adapter = adapter

        val backButton: ImageButton = binding.conversationBackButton
        backButton.setOnClickListener {
            finish()
        }

        val senderTextView: TextView = binding.tabMessageSender
        senderTextView.text = intent.getStringExtra("USERNAME") ?: "undefined"
        conversationId = intent.getIntExtra("CONVERSATION_ID", 0)
        messageInput = binding.messageInput

        initWebSocket()

        val sendButton = binding.sendMessageButton
        sendButton.setOnClickListener {
            sendMessageViaWebSocket(messageInput.text.toString())
        }

        loadConversationMessages()

    }

    private fun loadConversationMessages() {
        messageRepository.getMessagesByConversation(conversationId,
            { conversations ->
                val items = conversations.map {
                    val sender: MessageSender = when (it.senderId) {
                        sharedPref.getInt("USER_ID", -1) -> MessageSender.ME;
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
            sharedPref.getInt("USER_ID", -1),
            1,
            messageInput.text.toString(),
            conversationId = conversationId,
            createdAt = currentDate,
            isSystemMessage = false

        )
        messageRepository.sendMessageToConversation(message,
            onSuccess = {
                messageInput.setText("")
                loadConversationMessages()
            },
            onError = { error ->
                Log.e("ERROR", "sendMessageToConversation: ${error.message}")
                Toast.makeText(applicationContext, "Erreur : ${error.message}", Toast.LENGTH_SHORT)
                    .show()
                updateRecyclerView(emptyList())
            })
    }

    private lateinit var webSocket: WebSocket

    private fun initWebSocket() {
        val client = OkHttpClient()
        val request =
            Request.Builder().url("ws://10.0.2.2:8080/messages/ws/conversation/$conversationId")
                .build()
        val listener = ChatWebSocketListener({ newMessage ->
            runOnUiThread {

                updateRecyclerView(adapter.messages + newMessage)
            }
        }, sharedPref.getInt("USER_ID", -1))
        webSocket = client.newWebSocket(request, listener)
    }

    private fun sendMessageViaWebSocket(text: String) {
        if (text.isNotEmpty()) {
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            val message: Message = Message(
                sharedPref.getInt("USER_ID", -1),
                1,
                messageInput.text.toString(),
                conversationId = conversationId,
                createdAt = currentDate,
                isSystemMessage = false
            )
            val jsonMessage = Gson().toJson(message)
            webSocket.send(jsonMessage)
            val messageItem = MessageItem(message = text, sender = MessageSender.ME)
            messageInput.setText("")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, "Activity destroyed")
    }


    fun updateRecyclerView(items: List<MessageItem>) {
        adapter.updateItems(items)
        binding.conversationRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }
}



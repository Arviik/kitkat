package com.example.kitkat.ui.message.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R
import com.example.kitkat.databinding.ActivityConversationBinding
import com.example.kitkat.model.ConversationItem
import com.example.kitkat.model.MessageItem
import com.example.kitkat.model.MessageSender
import com.example.kitkat.ui.message.MessageConversationAdapter

class ConversationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConversationBinding

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
        recyclerView.adapter = ConversationAdapter(getSampleConversation())

        val backButton: TextView = binding.conversationBackButton
        backButton.setOnClickListener{
            finish()
        }

        val senderTextView: TextView = binding.tabMessageSender
        senderTextView.text = intent.getStringExtra("USERNAME") ?: "undefined"
    }

    private fun getSampleConversation(): List<MessageItem>{
        return listOf(
            MessageItem("Today", MessageSender.INFO),
            MessageItem("Salut", MessageSender.ME),
            MessageItem("Salut cv ?", MessageSender.THEM),
            MessageItem("oe", MessageSender.ME),
            MessageItem("trkl et toi ?", MessageSender.ME),
            MessageItem("trkl", MessageSender.THEM),MessageItem("Today", MessageSender.INFO),
            MessageItem("Salut", MessageSender.ME),
            MessageItem("Salut cv ?", MessageSender.THEM),
            MessageItem("oe", MessageSender.ME),
            MessageItem("trkl et toi ?", MessageSender.ME),
            MessageItem("trkl", MessageSender.THEM),MessageItem("Today", MessageSender.INFO),
            MessageItem("Salut", MessageSender.ME),
            MessageItem("Salut cv ?", MessageSender.THEM),
            MessageItem("oe", MessageSender.ME),
            MessageItem("trkl et toi ?", MessageSender.ME),
            MessageItem("trkl", MessageSender.THEM),MessageItem("Today", MessageSender.INFO),
            MessageItem("Salut", MessageSender.ME),
            MessageItem("Salut cv ?", MessageSender.THEM),
            MessageItem("oe", MessageSender.ME),
            MessageItem("trkl et toi ?", MessageSender.ME),
            MessageItem("trkl", MessageSender.THEM),MessageItem("Today", MessageSender.INFO),
            MessageItem("Salut", MessageSender.ME),
            MessageItem("Salut cv ?", MessageSender.THEM),
            MessageItem("oe", MessageSender.ME),
            MessageItem("trkl et toi ?", MessageSender.ME),
            MessageItem("trkl", MessageSender.THEM),MessageItem("Today", MessageSender.INFO),
            MessageItem("Salut", MessageSender.ME),
            MessageItem("Salut cv ?", MessageSender.THEM),
            MessageItem("oe", MessageSender.ME),
            MessageItem("trkl et toi ?", MessageSender.ME),
            MessageItem("trkl", MessageSender.THEM),
        )
    }
}


package com.example.kitkat.ui.message

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R
import com.example.kitkat.databinding.FragmentMessageBinding
import com.example.kitkat.model.ConversationItem
import com.example.kitkat.ui.comment.CommentAdapter
import com.example.kitkat.ui.message.conversation.ConversationActivity

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_message, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.messageRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MessageConversationAdapter(getSampleConversation(), onConversationClick)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val messageViewModel =
            ViewModelProvider(this).get(MessageViewModel::class.java)

        _binding = FragmentMessageBinding.inflate(inflater, container, false)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getSampleConversation(): List<ConversationItem>{
        return listOf(
            ConversationItem("Shokkzu", "Salut","1d",""),
            ConversationItem("Talal", "HMMMM","1d",""),
            ConversationItem("dadadada", "Salut","1d",""),
            ConversationItem("Shokdadadazfezakzu", "Salut","1d",""),
        )
    }

    private val onConversationClick = { username: String, id: String ->
        val intent = Intent(context, ConversationActivity::class.java)
        intent.putExtra("USERNAME", username)
        startActivity(intent)
    }
}

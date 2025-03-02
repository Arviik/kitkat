package com.example.kitkat.ui.message

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitkat.R
import com.example.kitkat.databinding.FragmentMessageBinding
import com.example.kitkat.model.ConversationItem
import com.example.kitkat.repositories.ConversationRepository
import com.example.kitkat.ui.message.conversation.ConversationActivity

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private val conversationRepository = ConversationRepository()
    private lateinit var adapter: MessageConversationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val view = binding.root

        val recyclerView: RecyclerView = binding.messageRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = MessageConversationAdapter(emptyList(), onConversationClick)
        recyclerView.adapter = adapter

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.messageRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MessageConversationAdapter(emptyList(), onConversationClick)
        recyclerView.adapter = adapter

        loadUserConversations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadUserConversations() {
        conversationRepository.getConversationsByUser(2,
            { conversations ->
                if (isAdded) { // Vérifie si le fragment est encore attaché
                    val items = conversations.map {
                        ConversationItem(
                            username = "${it.username}",
                            lastMessage = "${it.lastMessage}",
                            profilePicUrl = ""
                        )
                    }
                    updateRecyclerView(items)
                }
            },
            { error ->
                if (isAdded) {
                    Log.e("ERROR", "loadUserConversations: ${error.message}")
                    Toast.makeText(requireContext(), "Erreur : ${error.message}", Toast.LENGTH_SHORT).show()
                    updateRecyclerView(emptyList())
                }
            }
        )
    }


    private fun updateRecyclerView(items: List<ConversationItem>) {
        adapter.updateItems(items)
    }

    private val onConversationClick = { username: String, id: String ->
        val intent = Intent(context, ConversationActivity::class.java)
        intent.putExtra("USERNAME", username)
        intent.putExtra("CONVERSATION_ID", id)
        startActivity(intent)
    }
}

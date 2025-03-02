package com.example.kitkat.ui.message

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.kitkat.app_utils.SHARED_PREF_KEY
import com.example.kitkat.databinding.FragmentMessageBinding
import com.example.kitkat.model.ConversationItem
import com.example.kitkat.repositories.ConversationRepository
import com.example.kitkat.ui.message.conversation.ConversationActivity

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref: SharedPreferences
    private val conversationRepository = ConversationRepository()
    private lateinit var adapter: MessageConversationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val view = binding.root
        sharedPref = requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)


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
        conversationRepository.getConversationsByUser(sharedPref.getInt("USER_ID", -1),
            { conversations ->
                if (isAdded) { // Vérifie si le fragment est encore attaché
                    val items = conversations.map {
                        ConversationItem(
                            id = it.id,
                            username = it.username ?: "",
                            lastMessage = it.lastMessage ?: "",
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

    private val onConversationClick = { username: String, id: Int ->
        val intent = Intent(context, ConversationActivity::class.java)
        intent.putExtra("USERNAME", username)
        intent.putExtra("CONVERSATION_ID", id)
        startActivity(intent)
    }
}

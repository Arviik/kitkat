package com.example.kitkat.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kitkat.MainActivity
import com.example.kitkat.R
import com.example.kitkat.app_utils.SHARED_PREF_KEY
import com.example.kitkat.network.dto.LoginRequestDTO
import com.example.kitkat.network.dto.UserDTO
import com.example.kitkat.repositories.UserRepository

class UsernameInputFragment : Fragment() {
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_username_input, container, false)
        userRepository = UserRepository(requireContext())

        val button = view.findViewById<Button>(R.id.button_complete)
        val usernameInput = view.findViewById<EditText>(R.id.edit_text_username)

        button.setOnClickListener {
            if (usernameInput.text.isNotEmpty()) {
                val sharedPref = activity?.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE) ?: return@setOnClickListener

                val email = sharedPref.getString("email", "defaultEmail").toString()
                val password = sharedPref.getString("password", "defaultPassword").toString()
                val username = usernameInput.text.toString()

                println("email: $email password: $password username: $username")

                userRepository.registerUser(
                    UserDTO(
                        name = username,
                        email = email,
                        password = password
                    ),
                    onSuccess = {
                        Toast.makeText(context, "Inscription réussie, connexion en cours...", Toast.LENGTH_SHORT).show()
                        navigateToMainActivity()
                    },
                    onError = {
                        Toast.makeText(context, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(context, "Veuillez entrer un pseudo", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}

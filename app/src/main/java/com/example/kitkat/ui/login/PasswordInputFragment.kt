package com.example.kitkat.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kitkat.R
import com.example.kitkat.app_utils.SHARED_PREF_KEY

class PasswordInputFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_password_input, container, false)

        val button = view.findViewById<Button>(R.id.button_password_continue)
        val passwordInput = view.findViewById<EditText>(R.id.edit_text_password)

        button.setOnClickListener {
            if (passwordInput.text.length >= 8) {
                val sharedPref = activity?.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE) ?: return@setOnClickListener
                with(sharedPref.edit()) {
                    putString("password", passwordInput.text.toString())
                    apply()
                }
                findNavController().navigate(R.id.action_passwordInputFragment_to_usernameInputFragment)
            } else {
                Toast.makeText(context, "Mot de passe trop court", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}

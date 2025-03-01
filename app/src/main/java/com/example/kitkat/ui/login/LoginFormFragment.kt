package com.example.kitkat.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kitkat.R
import com.example.kitkat.app_utils.SHARED_PREF_KEY
import com.example.kitkat.network.dto.LoginRequestDTO
import com.example.kitkat.repositories.UserRepository

class LoginFormFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login_form, container, false)

        val button = view.findViewById<Button>(R.id.button_continue)
        val emailInput = view.findViewById<EditText>(R.id.edit_text_email)
        val passwordInput = view.findViewById<EditText>(R.id.edit_text_password)

        val registrationLink = view.findViewById<TextView>(R.id.registration_link)

        button.setOnClickListener {
            if (emailInput.text.isEmpty()) {
                Toast.makeText(context, "Veuillez entrer un email valide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordInput.text.isEmpty()) {
                Toast.makeText(context, "Veuillez entrer un mot de passe valide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            UserRepository.loginUser(
                LoginRequestDTO(
                    email = emailInput.text.toString(),
                    password = passwordInput.text.toString()
                ),
                onSuccess = {
                    val sharedPref = activity?.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE) ?: return@loginUser
                    with(sharedPref.edit()) {
                        putString("AUTH_TOKEN", it.token)
                        apply()
                    }

                    //TODO redirect to main app

                    //findNavController().navigate(R.id.)
                },
                onError = {
                    Toast.makeText(context, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show()
                }
            )
        }

        registrationLink.setOnClickListener {
            //TODO redirect to registration

            //findNavController().navigate(R.id.)
        }

        return view
    }
}
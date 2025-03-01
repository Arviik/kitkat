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

class EmailInputFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_email_input, container, false)

        val button = view.findViewById<Button>(R.id.button_continue)
        val emailInput = view.findViewById<EditText>(R.id.edit_text_email)

        button.setOnClickListener {
            if (emailInput.text.isNotEmpty()) {
                val sharedPref = activity?.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE) ?: return@setOnClickListener
                with(sharedPref.edit()) {
                    putString("email", emailInput.text.toString())
                    apply()
                }
                findNavController().navigate(R.id.action_emailInputFragment_to_codeVerificationFragment)
            } else {
                Toast.makeText(context, "Veuillez entrer un email valide", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}

package com.example.kitkat.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kitkat.R

class UsernameInputFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_username_input, container, false)

        val button = view.findViewById<Button>(R.id.button_complete)
        val usernameInput = view.findViewById<EditText>(R.id.edit_text_username)

        button.setOnClickListener {
            if (usernameInput.text.isNotEmpty()) {
                Toast.makeText(context, "Inscription terminée !", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Veuillez entrer un pseudo", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}

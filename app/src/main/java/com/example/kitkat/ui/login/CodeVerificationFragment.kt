package com.example.kitkat.ui.login

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

class CodeVerificationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_code_verification, container, false)

        val button = view.findViewById<Button>(R.id.button_verify)
        val codeInput = view.findViewById<EditText>(R.id.edit_text_code)

        button.setOnClickListener {
            if (codeInput.text.length == 6) {
                findNavController().navigate(R.id.action_codeVerificationFragment_to_passwordInputFragment)
            } else {
                Toast.makeText(context, "Veuillez entrer un code valide", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}

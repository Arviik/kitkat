package com.example.kitkat.app_utils

import android.util.Log
import com.auth0.android.jwt.JWT

fun isTokenExpired(token: String): Boolean {
    return try {
        val jwt = JWT(token)
        val expirationTime = jwt.expiresAt?.time
        Log.d("MainActivity", "Expiration du token: $expirationTime - Temps actuel: ${System.currentTimeMillis()}")

        if (expirationTime == null) {
            Log.e("MainActivity", "Le token n'a pas de date d'expiration !")
            return true
        }

        return expirationTime < System.currentTimeMillis()
    } catch (e: Exception) {
        Log.e("MainActivity", "Erreur lors de la vérification du token: ${e.message}")
        true
    }
}

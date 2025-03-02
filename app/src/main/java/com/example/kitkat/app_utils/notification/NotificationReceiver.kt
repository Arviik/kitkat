// NotificationReceiver.kt
package com.example.kitkat.app_utils.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Notification"
        val message = intent.getStringExtra("message") ?: "Message par défaut"

        NotificationHelper.showNotification(context, title, message)
    }
}

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.kitkat.app_utils.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class ApiNotificationService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val apiUrl = "https://example.com/notification" // Ton URL d'API

        serviceScope.launch {
            fetchNotification(apiUrl)
        }

        return START_STICKY
    }

    private suspend fun fetchNotification(apiUrl: String) {
        try {
            val url = URL(apiUrl)
            val connection = withContext(Dispatchers.IO) {
                url.openConnection()
            } as HttpURLConnection
            connection.requestMethod = "GET"

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val title = connection.getHeaderField("Notification-Title") ?: "Titre par défaut"
                val message = connection.getHeaderField("Notification-Message") ?: "Message par défaut"

                NotificationHelper.showNotification(applicationContext, title, message)
            }
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Nettoie les coroutines lorsqu'on détruit le service
    }
}

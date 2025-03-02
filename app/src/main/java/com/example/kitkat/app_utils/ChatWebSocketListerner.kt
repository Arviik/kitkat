import android.util.Log
import com.example.kitkat.model.MessageItem
import com.example.kitkat.model.MessageSender
import com.example.kitkat.network.dto.Message
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString

class ChatWebSocketListener(
    private val onMessageReceived: (MessageItem) -> Unit,
    private val userId: Int
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocket", "Connexion ouverte")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocket", "Message reçu : $text")
        // Convertis le message JSON en MessageItem si besoin
        val message: Message = Gson().fromJson(text, Message::class.java)
        val sender: MessageSender = when (message.senderId) {
            userId -> MessageSender.ME;
            else -> {
                if (message.isSystemMessage) {
                    MessageSender.INFO
                } else {
                    MessageSender.THEM
                }
            }
        }

        val messageItem = MessageItem(message = message.content, sender = sender)
        onMessageReceived(messageItem)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocket", "Erreur : ${t.message}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        Log.d("WebSocket", "Fermeture du socket : $code / $reason")
    }
}

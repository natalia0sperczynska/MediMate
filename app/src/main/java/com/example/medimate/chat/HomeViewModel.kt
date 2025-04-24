import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.medimate.chat.ChatMessage
import com.example.medimate.chat.SenderType

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> get() = _messages

    fun sendMessage(text: String, sender: SenderType) {
        if (text.isNotBlank()) {
            val message = ChatMessage(sender = sender, message = text)
            _messages.add(message)
        }
    }

}

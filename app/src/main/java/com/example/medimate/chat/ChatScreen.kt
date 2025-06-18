package com.example.medimate.chat

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.medimate.ui.theme.MediMateTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("SimpleDateFormat")
@Composable
fun ChatScreen(
    targetUserId: String,
    chatRepository: ChatRepository = ChatRepository()
) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val messages by chatRepository.getMessagesFlow(currentUserId, targetUserId).collectAsState(initial = emptyList())
    var inputText by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var fileUri by remember { mutableStateOf<Uri?>(null) }
    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        fileUri = uri
    }
    val isTyping by chatRepository.observeTypingStatus(currentUserId, targetUserId)
        .collectAsState(initial = false)
    if (isTyping) {
        Text("Doctor is typing...", style = MaterialTheme.typography.labelSmall)
    }

    LaunchedEffect(Unit) {
        chatRepository.markMessagesAsRead(currentUserId, targetUserId)
    }

    LaunchedEffect(inputText) {
        if (inputText.isNotEmpty()) {
            chatRepository.setTypingStatus(currentUserId, targetUserId, true)
            delay(1000) // Debounce
            chatRepository.setTypingStatus(currentUserId, targetUserId, false)
        } else {
            chatRepository.setTypingStatus(currentUserId, targetUserId, false)
        }
    }

    LaunchedEffect(fileUri) {
        fileUri?.let { uri ->
            sending = true
            try {
                val (url, fileType) = chatRepository.uploadFile(uri, currentUserId)
                chatRepository.sendMessage(currentUserId, targetUserId, "", fileUrl = url, fileType = fileType)
                Toast.makeText(context, "File sent!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            fileUri = null
            sending = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        if (isTyping){
            Text(
                text = "Doctor is typing...",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f).padding(8.dp),
            reverseLayout = false
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = if (msg.senderId == currentUserId) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (msg.senderId == currentUserId)
                                Color(0xFF9C7CBC) else (Color.White)
                        ),
                        shape = if (msg.senderId == currentUserId)
                            MaterialTheme.shapes.medium.copy(
                                topEnd = CornerSize(0.dp),
                                bottomStart = CornerSize(16.dp),
                                bottomEnd = CornerSize(16.dp),
                                topStart = CornerSize(16.dp)
                            ) else MaterialTheme.shapes.medium.copy(
                            topStart = CornerSize(0.dp),
                            bottomStart = CornerSize(16.dp),
                            bottomEnd = CornerSize(16.dp),
                            topEnd = CornerSize(16.dp)
                        )
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            if (msg.text.isNotEmpty()) Text(msg.text)
                            msg.fileUrl?.let { url ->
                                if (msg.fileType == "image") {
                                    AsyncImage(url, contentDescription = null, modifier = Modifier.size(160.dp))
                                } else {
                                    Text("File: $url", color = Color.Blue)
                                }
                            }
                            Text(
                                text = java.text.SimpleDateFormat("HH:mm").format(java.util.Date(msg.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
        HorizontalDivider()
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { filePickerLauncher.launch("image/*") }) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach file")
            }
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") },
                enabled = !sending
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        sending = true
                        chatRepository.sendMessage(currentUserId, targetUserId, inputText)
                        inputText = ""
                        sending = false
                    }
                },
                enabled = inputText.isNotBlank() && !sending,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Send")
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    MediMateTheme {
        ChatScreen(
            targetUserId = "previewUserId",
            chatRepository = ChatRepository()
        )
    }
}
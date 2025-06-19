package com.example.medimate.chat

import android.net.Uri
import com.example.medimate.firebase.Message
import com.example.medimate.firebase.appointment.Status
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
//    private val auth = FirebaseAuth.getInstance()

    private fun getChatId(user1: String, user2: String): String {
        return listOf(user1, user2).sorted().joinToString("_")
    }

    fun getMessagesFlow(currentUserId: String, targetUserId: String): Flow<List<Message>> = callbackFlow {
        val chatId = getChatId(currentUserId, targetUserId)
        val ref = db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
        val listener = ref.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val messages = snapshot.documents.mapNotNull { it.toObject(Message::class.java)?.copy(id = it.id) }
                trySend(messages)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(currentUserId: String, targetUserId: String, text: String, fileUrl: String? = null, fileType: String? = null) {
        val chatId = getChatId(currentUserId, targetUserId)
        val message = Message(
            senderId = currentUserId,
            receiverId = targetUserId,
            text = text,
            fileUrl = fileUrl,
            fileType = fileType,
            timestamp = System.currentTimeMillis(),
            status = Status.SENT
        )

        db.collection("chats").document(chatId).set(mapOf(
            "participants" to listOf(currentUserId, targetUserId),
            "lastUpdated" to System.currentTimeMillis(),
            "lastMessage" to message.text.take(50),
            "${currentUserId}_typing" to false // Reset typing status when sending
        )).await()

        db.collection("chats").document(chatId).collection("messages").add(message).await()
    }

    suspend fun uploadFile(fileUri: Uri, currentUserId: String): Pair<String, String> {
        val ext = fileUri.lastPathSegment?.substringAfterLast('.') ?: "file"
        val fileType = when (ext.lowercase()) {
            "jpg", "jpeg", "png", "gif" -> "image"
            "pdf", "doc", "docx" -> "document"
            else -> "file"
        }
        val storageRef = storage.reference.child("chat_files/$currentUserId/${System.currentTimeMillis()}.$ext")
        storageRef.putFile(fileUri).await()
        return Pair(storageRef.downloadUrl.await().toString(), fileType)
    }

    fun observeTypingStatus(currentUserId: String, targetUserId: String): Flow<Boolean> = callbackFlow {
        val chatId = getChatId(currentUserId, targetUserId)
        val ref = db.collection("chats").document(chatId)
        val listener = ref.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val isTyping = snapshot.getBoolean("${targetUserId}_typing") ?: false
                trySend(isTyping)
            }
        }
        awaitClose { listener.remove() }
    }
    suspend fun setTypingStatus(currentUserId: String, targetUserId: String, isTyping: Boolean) {
        val chatId = getChatId(currentUserId, targetUserId)
        db.collection("chats").document(chatId)
            .set(mapOf("${currentUserId}_typing" to isTyping), SetOptions.merge())
            .await()
    }

    suspend fun markMessagesAsRead(currentUserId: String, targetUserId: String) {
        val chatId = getChatId(currentUserId, targetUserId)
        db.collection("chats").document(chatId).collection("messages")
            .whereEqualTo("receiverId", currentUserId)
            .whereEqualTo("status", Status.DELIVERED)
            .get()
            .await()
            .forEach { doc ->
                doc.reference.update("status", Status.READ).await()
            }
    }
}
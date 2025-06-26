package com.example.medimate.chat

import android.net.Uri
import com.example.medimate.firebase.Message
import com.example.medimate.firebase.appointment.Status
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Repository class responsible for handling chat-related operations, including sending messages,
 * observing chat updates, uploading files, managing typing status, and marking messages as read.
 */
class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    /**
     * Generates a unique chat ID based on two user IDs by sorting and joining them.
     *
     * @param user1 The first user ID.
     * @param user2 The second user ID.
     * @return A consistent chat ID for the given users.
     */
    private fun getChatId(user1: String, user2: String): String {
        return listOf(user1, user2).sorted().joinToString("_")
    }

    /**
     * Provides a real-time flow of messages for the chat between the current and target user.
     *
     * @param currentUserId The ID of the current user.
     * @param targetUserId The ID of the target (chat partner) user.
     * @return A [Flow] that emits the list of messages whenever they change.
     */
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

    /**
     * Sends a message to the target user, with optional file attachment.
     *
     * @param currentUserId The ID of the sender.
     * @param targetUserId The ID of the receiver.
     * @param text The message text.
     * @param fileUrl Optional URL of an attached file.
     * @param fileType Optional type of the file (e.g., "image", "document").
     */
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
            "${currentUserId}_typing" to false
        )).await()

        db.collection("chats").document(chatId).collection("messages").add(message).await()
    }

    /**
     * Uploads a file to Firebase Storage and returns its URL along with a detected file type.
     *
     * @param fileUri The URI of the file to upload.
     * @param currentUserId The ID of the uploading user.
     * @return A [Pair] containing the download URL and file type ("image", "document", or "file").
     */
    suspend fun uploadFile(fileUri: Uri, currentUserId: String): Pair<String, String> {
        val ext = fileUri.lastPathSegment?.substringAfterLast('.') ?: "file"
        val fileType = when (ext.lowercase()) {
            "jpg", "jpeg", "png", "gif" -> "image"
            "pdf", "doc", "docx" -> "document"
            else -> "file"
        }
        val storage = Firebase.storage("gs://medimate-79d20.firebasestorage.app")
        val filename = "profile_${System.currentTimeMillis()}"
        val storageRef = storage.reference.child("chat_files/$currentUserId/${System.currentTimeMillis()}.$ext")
        val downloadUrl = storageRef.downloadUrl.await().toString()
        storageRef.putFile(fileUri).await()
        return Pair(downloadUrl, fileType)
    }

    /**
     * Observes the typing status of the target user in the current chat.
     *
     * @param currentUserId The ID of the observing user.
     * @param targetUserId The ID of the user whose typing status is being observed.
     * @return A [Flow] emitting `true` if the target user is typing, `false` otherwise.
     */
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
    /**
     * Sets the typing status for the current user in the chat with the target user.
     *
     * @param currentUserId The ID of the user whose typing status is being set.
     * @param targetUserId The ID of the chat partner.
     * @param isTyping `true` if the user is currently typing, `false` otherwise.
     */
    suspend fun setTypingStatus(currentUserId: String, targetUserId: String, isTyping: Boolean) {
        val chatId = getChatId(currentUserId, targetUserId)
        db.collection("chats").document(chatId)
            .set(mapOf("${currentUserId}_typing" to isTyping), SetOptions.merge())
            .await()
    }

    /**
     * Marks all delivered messages sent to the current user by the target user as read.
     *
     * @param currentUserId The ID of the message receiver (i.e., current user).
     * @param targetUserId The ID of the message sender (chat partner).
     */
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
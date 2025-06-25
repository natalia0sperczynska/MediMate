package com.example.medimate.firebase

import com.example.medimate.firebase.appointment.Status

/**
 * Data class representing a chat message between users.
 *
 * @property id The unique identifier for the message.
 * @property senderId The ID of the user who sent the message.
 * @property receiverId The ID of the user who received the message.
 * @property text The text content of the message (empty if file message).
 * @property fileUrl URL of the attached file (null if text message).
 * @property fileType MIME type of the attached file (e.g., "image/jpeg", "application/pdf").
 * @property timestamp Unix timestamp when the message was created.
 * @property status Current delivery status of the message (SENT, DELIVERED, READ).
 */
data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val fileUrl: String? = null,
    val fileType: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val status: Status = Status.SENT
)
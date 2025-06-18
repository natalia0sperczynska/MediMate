package com.example.medimate.firebase

import com.example.medimate.firebase.appointment.Status

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
package com.example.medimate.chat

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: SenderType,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

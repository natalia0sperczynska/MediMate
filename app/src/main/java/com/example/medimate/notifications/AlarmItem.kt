package com.example.medimate.notifications

import java.time.LocalDateTime
data class AlarmItem(
    val time: LocalDateTime,
    val message: String
)

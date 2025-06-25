package com.example.medimate.notifications

import java.time.LocalDateTime

/**
 * Represents an alarm item with a scheduled time and a message.
 *
 * @property time The date and time of the alarm.
 * @property message The message to display when the alarm triggers.
 */
data class AlarmItem(
    val time: LocalDateTime,
    val message: String
)

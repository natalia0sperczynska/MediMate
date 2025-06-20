package com.example.medimate.firebase.review

data class Review(
    val rate: Double= 0.0,
    val text: String = "",
    val userId: String = "",
    val doctorId: String ="",
    val timestamp: Long = System.currentTimeMillis()) {

    init {
        require(rate in 0.0..5.0) { "Rating must be between 0-5" }
        require(text.length <= 500) { "Review text too long" }
    }
    companion object {
        fun fromMap(data: Map<String, Any>): Review {
            return Review(
                rate = data["rate"] as? Double ?: 0.0,
                text = data["text"] as? String ?: "",
                userId = data["userId"] as? String ?: "",
                doctorId = data["doctorId"] as? String ?: "",
                timestamp = data["timestamp"] as? Long ?: System.currentTimeMillis()
            )
        }

        fun toMap(review: Review): Map<String, Any> {
            return mapOf(
                "rate" to review.rate,
                "text" to review.text,
                "userId" to review.userId,
                "doctorId" to review.doctorId,
                "timestamp" to review.timestamp
            )
        }
    }
}

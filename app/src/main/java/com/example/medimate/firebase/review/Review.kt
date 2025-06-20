package com.example.medimate.firebase.review

data class Review(
    val rate: Double= 0.0,
    val text: String = "",
    val userId: String = "",
    val doctorId: String ="") {

    init {
        require(rate in 0.0..5.0) { "Rating must be between 0-5" }
        require(text.length <= 500) { "Review text too long" }
    }
}

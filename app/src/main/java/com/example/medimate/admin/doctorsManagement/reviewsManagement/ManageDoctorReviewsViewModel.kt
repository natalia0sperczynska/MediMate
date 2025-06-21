package com.example.medimate.admin.doctorsManagement.reviewsManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medimate.firebase.review.Review
import com.example.medimate.firebase.review.ReviewDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class ManageDoctorReviewsViewModel (
    private val reviewDAO: ReviewDAO = ReviewDAO()
) : ViewModel() {
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    fun loadReviews(doctorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _reviews.value = reviewDAO.getReviewsForDoctor(doctorId)
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteReview(doctorId: String, review: Review) {
        viewModelScope.launch {
            _isDeleting.value = true
            try {
                val currentReviews = _reviews.value.toMutableList()
                currentReviews.remove(review)
                val newRating = if (currentReviews.isNotEmpty()) {
                    currentReviews.map { it.rate }.average()
                } else {
                    0.0
                }
                reviewDAO.deleteReview(doctorId, review, currentReviews, newRating)
                _reviews.value = currentReviews
            } finally {
                _isDeleting.value = false
            }
        }
    }
}
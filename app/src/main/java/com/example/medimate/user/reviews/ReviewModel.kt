package com.example.medimate.user.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.firebase.review.Review
import com.example.medimate.firebase.review.ReviewDAO
import com.example.medimate.user.doctorsView.getDoctorList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class ReviewModel: ViewModel(){
    private val reviewDAO = ReviewDAO()
    private val doctorDAO = DoctorDAO()

    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    private val _reviews = MutableStateFlow<List<Review>?>(emptyList())
    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _reviewAdded = MutableStateFlow(false)


    open val doctors: StateFlow<List<Doctor>> get() = _doctors
    open val reviews: MutableStateFlow<List<Review>?> get() = _reviews
    val selectedDoctor: StateFlow<Doctor?> get() = _selectedDoctor
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val reviewAdded: StateFlow<Boolean> = _reviewAdded.asStateFlow()


    open fun loadDoctorById(doctorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedDoctor.value = doctorDAO.getDoctorById(doctorId)
            _reviews.value = reviewDAO.GetReviewsForDoctor(doctorId)
            _isLoading.value = false
        }
    }
    fun addReview(review: Review) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                reviewDAO.AddReview(review.doctorId, review)
                _reviewAdded.value = true
                _reviews.value = reviewDAO.GetReviewsForDoctor(review.doctorId)
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun resetReviewAdded() {
        _reviewAdded.value = false
    }
    init {
        viewModelScope.launch {
            _doctors.value = getDoctorList()
        }
    }

}
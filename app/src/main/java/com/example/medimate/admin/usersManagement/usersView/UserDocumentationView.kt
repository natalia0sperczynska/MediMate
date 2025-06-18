package com.example.medimate.admin.usersManagement.usersView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medimate.firebase.user.User
import com.example.medimate.firebase.user.UserDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserDocumentationViewModel : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    private val _isLoading = MutableStateFlow(false)

    val user = _user.asStateFlow()
    val isLoading = _isLoading.asStateFlow()

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = UserDAO().getUserById(userId)
                _user.value = user
            } catch (e: Exception) {

            } finally {
                _isLoading.value = false
            }
        }
    }
}
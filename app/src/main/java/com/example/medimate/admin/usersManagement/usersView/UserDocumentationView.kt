package com.example.medimate.admin.usersManagement.usersView

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medimate.firebase.user.User
import com.example.medimate.firebase.user.UserDAO
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserDocumentationViewModel : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()
    private var _errorMessage = MutableStateFlow<String?>(null)

    val user = _user.asStateFlow()
    val isLoading = _isLoading.asStateFlow()
    var errorMessage = _errorMessage.asStateFlow()

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val user = UserDAO().getUserById(userId)
                if (user == null) {
                    _errorMessage.value = "User not found"

                }
                _user.value = user?.copy()
            } catch (e: Exception) {
                Log.e("Error","${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun uploadUserDocument(userId: String, uri: Uri, context: Context) {
        viewModelScope.launch {
            _isUploading.value = true
            try {
                val storage = Firebase.storage("gs://medimate-79d20.firebasestorage.app")
                val filename = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
                val storageRef = storage.reference.child("user_documents/$userId/$filename")
                storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()
                val userDAO = UserDAO()
                val userMap = userDAO.getUserById(userId)
                val currentDocs = (userMap?.documents ?: listOf()).toMutableList()
                currentDocs.add(downloadUrl)
                userDAO.updateUserData(userId, mapOf("documents" to currentDocs))
                loadUserData(userId)
                Toast.makeText(context, "File uploaded!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isUploading.value = false
            }
        }
    }
}
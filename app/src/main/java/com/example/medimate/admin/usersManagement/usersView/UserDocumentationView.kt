package com.example.medimate.admin.usersManagement.usersView

import android.content.Context
import android.net.Uri
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
    fun uploadUserDocument(userId: String, uri: Uri, context: Context) {
        viewModelScope.launch {
            _isUploading.value = true
            try {
                val storageRef = Firebase.storage.reference.child("user_documents/$userId/${System.currentTimeMillis()}_${uri.lastPathSegment}")
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
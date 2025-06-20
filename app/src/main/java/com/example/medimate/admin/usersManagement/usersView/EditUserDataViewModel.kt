package com.example.medimate.admin.usersManagement.usersView
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medimate.firebase.admin.AdminDAO
import com.example.medimate.firebase.user.User
import com.example.medimate.firebase.user.UserDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class EditUserDataViewModel(private val adminDAO: AdminDAO = AdminDAO()) : ViewModel() {
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = adminDAO.getUserById(userId)
                _userState.value= user
            } catch (e: Exception) {
                e.message?.let { Log.e("error view model", it) }
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun updateName(name: String) {
        _userState.value = _userState.value?.copy(name = name)
    }

    fun updateSurname(surname: String) {
        _userState.value = _userState.value?.copy(surname = surname)
    }

    fun updateEmail(email: String) {
        _userState.value = _userState.value?.copy(email = email)
    }

    fun updateDateOfBirth(dateOfBirth: String) {
        _userState.value = _userState.value?.copy(dateOfBirth = dateOfBirth)
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _userState.value = _userState.value?.copy(phoneNumber = phoneNumber)
    }

    fun updateAddress(key: String, value: String) {
        val currentAddress = _userState.value?.address?.toMutableMap() ?: mutableMapOf()
        currentAddress[key] = value
        _userState.value = _userState.value?.copy(address = currentAddress)
    }

    fun updateAllergies(allergies: List<String>) {
        _userState.value = _userState.value?.copy(allergies = allergies)
    }

    fun updateDiseases(diseases: List<String>) {
        _userState.value = _userState.value?.copy(diseases = diseases)
    }

    fun updateMedications(medications: List<String>) {
        _userState.value = _userState.value?.copy(medications = medications)
    }

    fun saveChanges(userId: String) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                val user = _userState.value ?: return@launch
                if (user.name.isNullOrBlank() || user.email.isNullOrBlank()) {
                    throw Exception("Name and email are required")
                }
                adminDAO.updateUserData(userId, user.toMap())
            } catch (e: Exception) {
                throw e
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _isDeleting.value = true
            try {
                adminDAO.deleteUser(userId)
            } catch (e: Exception) {
                throw e
            } finally {
                _isDeleting.value = false
            }
        }
    }
}
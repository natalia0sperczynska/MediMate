package com.example.medimate.admin.doctorsManagement.editDoctorData
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medimate.firebase.admin.AdminDAO
import com.example.medimate.firebase.doctor.Doctor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditDoctorDataViewModel(private val adminDAO: AdminDAO = AdminDAO()) : ViewModel() {
    private val _doctorState = MutableStateFlow<Doctor?>(null)
    val doctorState: StateFlow<Doctor?> = _doctorState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    fun loadDoctorData(doctorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doctor = adminDAO.getDoctorById(doctorId)
                _doctorState.value= doctor
            } catch (e: Exception) {
                e.message?.let { Log.e("error view model", it) }
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun updateName(name: String) {
        _doctorState.value = _doctorState.value?.copy(name = name)
    }

    fun updateSurname(surname: String) {
        _doctorState.value = _doctorState.value?.copy(surname = surname)
    }

    fun updateEmail(email: String) {
        _doctorState.value = _doctorState.value?.copy(email = email)
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _doctorState.value = _doctorState.value?.copy(phoneNumber = phoneNumber)
    }

    fun updateSpecialisation(specialisation:String) {
        _doctorState.value = _doctorState.value?.copy(specialisation = specialisation)
    }

    fun updateRoom(room:String) {
        _doctorState.value = _doctorState.value?.copy(room=room)
    }

    fun saveChanges(doctorId: String) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                val doctor = _doctorState.value ?: return@launch
                if (doctor.name.isBlank() || doctor.email.isBlank()) {
                    throw Exception("Name and email are required")
                }
                adminDAO.updateDoctorData(doctorId, doctor.toMap())
            } catch (e: Exception) {
                throw e
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun deleteDoctor(doctorId: String) {
        viewModelScope.launch {
            _isDeleting.value = true
            try {
                adminDAO.deleteDoctor(doctorId)
            } catch (e: Exception) {
                throw e
            } finally {
                _isDeleting.value = false
            }
        }
    }
}
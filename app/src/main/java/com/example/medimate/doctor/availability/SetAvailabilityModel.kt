package com.example.medimate.doctor.availability

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.medimate.firebase.appointment.Term
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.doctor.DoctorDAO
import com.example.medimate.user.appointments.getAvailableTermsForDate
/**
 * Data class representing a doctor's availability settings.
 * Manages the current availability terms and provides methods to load, toggle, and save availability.
 *
 * @property currentAvailability The list of currently available terms.
 */
class SetAvailabilityModel {
    private val _currentAvailability = mutableStateListOf<Term>()
    val currentAvailability: List<Term> get() = _currentAvailability

    private var currentDate: String? = null
    private var currentDoctor: Doctor? = null

    fun setCurrentDoctor(doctor: Doctor) {
        currentDoctor = doctor
    }

    fun loadAvailability(terms: List<Term>, date: String) {
        _currentAvailability.clear()
        _currentAvailability.addAll(terms)
        currentDate = date
    }

    fun toggleTerm(index: Int) {
        if (index in _currentAvailability.indices) {
            val term = _currentAvailability[index]
            _currentAvailability[index] = term.copy(isAvailable = !term.isAvailable)
        }
    }

    fun getTermsForDate(dateString: String): List<Term> {
        return currentDoctor?.getAvailableTermsForDate(dateString) ?: emptyList()
    }

    suspend fun saveDayAvailability(context: Context): Boolean {
        return try {
            currentDoctor?.let { doctor ->
                currentDate?.let { date ->
                    DoctorDAO().updateDoctorAvailabilityNotApp(
                        doctor = doctor,
                        date = date,
                        terms = _currentAvailability
                    )
                    true
                } ?: false
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
}
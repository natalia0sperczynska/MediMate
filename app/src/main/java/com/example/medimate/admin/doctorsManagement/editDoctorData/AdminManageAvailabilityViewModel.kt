package com.example.medimate.admin.doctorsManagement.editDoctorData
import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medimate.firebase.appointment.Term
import com.example.medimate.firebase.doctor.Doctor
import com.example.medimate.firebase.doctor.DoctorDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
/**
 * ViewModel responsible for managing doctor availability in the admin panel.
 */
class AdminManageAvailabilityViewModel :ViewModel() {
    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> = _doctors.asStateFlow()

    private val _availability = MutableStateFlow<List<Term>>(emptyList())
    val availability: StateFlow<List<Term>> = _availability.asStateFlow()

    private var currentDoctor: Doctor? = null
    private var currentDate: String? = null

    init {
        loadAllDoctors()
    }

    /**
     * Loads all doctors from the database and populates [_doctors].
     */
    private fun loadAllDoctors() {
        viewModelScope.launch {
            try {
                _doctors.value = DoctorDAO().getAllDoctors()
            } catch (e: Exception) {

            }
        }
    }
    /**
     * Loads the availability of a specific doctor for a given date.
     * Falls back to the default schedule if no availability is set.
     *
     * @param doctor The doctor to load availability for.
     * @param date The date string in "MM/dd/yyyy" format.
     */
    fun loadDoctorAvailability(doctor: Doctor, date: String) {
        currentDoctor = doctor
        currentDate = date
        viewModelScope.launch {
            try {
                val terms = doctor.getAvailableTermsForDate(date)
                _availability.value = terms ?: getDefaultTermsForDate(doctor, date)
            } catch (e: Exception) {
                _availability.value = getDefaultTermsForDate(doctor, date)
            }
        }
    }
    /**
     * Returns the default availability based on the weekday in the doctor's predefined schedule.
     *
     * @param doctor The doctor whose default availability to return.
     * @param date The date string in "MM/dd/yyyy" format.
     */
    private fun getDefaultTermsForDate(doctor: Doctor, date: String): List<Term> {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val parsedDate = formatter.parse(date) ?: return emptyList()

        val calendar = Calendar.getInstance().apply { time = parsedDate }
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> doctor.availability.monday
            Calendar.TUESDAY -> doctor.availability.tuesday
            Calendar.WEDNESDAY -> doctor.availability.wednesday
            Calendar.THURSDAY -> doctor.availability.thursday
            Calendar.FRIDAY -> doctor.availability.friday
            Calendar.SATURDAY -> doctor.availability.saturday
            Calendar.SUNDAY -> doctor.availability.sunday
            else -> emptyList()
        }
    }
    /**
     * Toggles the availability of a specific time slot (term).
     *
     * @param index Index of the term in the list.
     */
    fun toggleTermAvailability(index: Int) {
        val current = _availability.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(isAvailable = !current[index].isAvailable)
            _availability.value = current
        }
    }
    /**
     * Saves the modified availability to the database.
     *
     * @return True if the operation was successful, false otherwise.
     */
    suspend fun saveAvailability(): Boolean {
        val doctor = currentDoctor ?: return false
        val date = currentDate ?: return false

        return try {
            DoctorDAO().updateDoctorAvailabilityNotApp(
                doctor= doctor,
                date = date,
                terms = _availability.value
            )
            true
        } catch (e: Exception) {
            false
        }
    }
}
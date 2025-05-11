package com.example.medimate.doctor.availability
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.medimate.firebase.doctor.Availability
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class SetAvailabilityModel : ViewModel() {
    private val _weekAvailability = mutableStateListOf<DayAvailabilityUI>()
    val weekAvailability: List<DayAvailabilityUI> get() = _weekAvailability

//    init {
//        _weekAvailability.addAll(
//            listOf(
//                "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"
//            ).map { day ->
//                DayAvailabilityUI(
//                    day = day,
//                    slots = Doctor.generateTimeSlots().map { Term(it.toString()) }
//                )
//            }
//        )
//    }

    fun toggleAvailability(day: String, index: Int) {
        _weekAvailability.find { it.day == day }?.slots?.get(index)?.let{
            it.isAvailable = !it.isAvailable
        }
    }
    fun saveAvailability(doctorId: String){
        val availability =  Availability(
        )
        Firebase.firestore.collection("doctors").document(doctorId).update("availability",availability)
    }
}
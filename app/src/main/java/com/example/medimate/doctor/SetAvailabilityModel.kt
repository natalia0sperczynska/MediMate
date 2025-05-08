package com.example.medimate.doctor

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.medimate.firebase.Availability
import com.example.medimate.firebase.Doctor
import com.example.medimate.firebase.Term
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
//            monday=getTermsForDay("monday"),
//            tuesday=getTermsForDay("tuesday"),
//            wednesday=getTermsForDay("wednesday"),
//            thursday=getTermsForDay("thursday"),
//            friday=getTermsForDay("friday"),
//            saturday=getTermsForDay("saturday"),
//            sunday=getTermsForDay("sunday")
        )
        Firebase.firestore.collection("doctors").document(doctorId).update("availability",availability)
    }
//    private fun getTermsForDay(day: String): List<Term> {
//        return _weekAvailability.find{it.day==day}?.slots?.filter{
//            it.isAvailable
//        }?.map{
//            it.term
//        }?: emptyList()
//        }
}
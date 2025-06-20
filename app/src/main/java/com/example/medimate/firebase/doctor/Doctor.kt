package com.example.medimate.firebase.doctor

import com.example.medimate.firebase.appointment.Term
import com.example.medimate.firebase.review.Review
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
//ciekawostki
/**
 * Data class representing a Doctor.
 *
 * @property id Unique identifier for the doctor.
 * @property name The doctor's first name.
 * @property surname The doctor's last name.
 * @property email The doctor's email address.
 * @property phoneNumber The doctor's phone number.
 * @property profilePicture URL to the doctor's profile picture.
 *
 */

data class Doctor(
    var id: String = "",
    var name: String = "",
    var surname: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var profilePicture: String = "",
    var specialisation:String = "",
    var room:String = "",
    var availability: Availability = Availability(),
    var availabilityChanges: Map<String, List<Term>> = emptyMap(),
    var rating: Double =0.0,
    var reviews:List<Review> = emptyList()

) {
    fun getAvailableTermsForDate(date: String): List<Term> {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault())
        val localDate = LocalDate.parse(date, formatter)

        availabilityChanges[date]?.let { return it }

        return availability.getDefaultTermsForDay(localDate.dayOfWeek)
    }
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$name$surname",
            "$name $surname",
            "${name.first()}${surname.first()}"
        )
        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
    fun withUpdatedAvailability(date: String, startTime: String, endTime: String, isAvailable: Boolean): Doctor {
        val updatedTerms = availabilityChanges[date]?.map { term ->
            if (term.startTime == startTime && term.endTime == endTime) {
                term.copy(isAvailable = isAvailable)
            } else {
                term
            }
        } ?: emptyList()

        return this.copy(
            availabilityChanges = availabilityChanges + (date to updatedTerms)
        )
    }
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "surname" to surname,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "specialisation" to specialisation,
            "room" to room
        )
    }

}


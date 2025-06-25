package com.example.medimate.firebase.doctor

import com.example.medimate.firebase.appointment.Term
import com.example.medimate.firebase.review.Review
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
/**
 * Data class representing a medical doctor.
 *
 * @property id The unique identifier for the doctor.
 * @property name The first name of the doctor.
 * @property surname The last name of the doctor.
 * @property email The email address of the doctor.
 * @property phoneNumber The phone number of the doctor.
 * @property profilePicture URL to the doctor's profile picture.
 * @property specialisation The medical specialization of the doctor.
 * @property room The room number where the doctor sees patients.
 * @property availability The default weekly availability schedule.
 * @property availabilityChanges Map of date-specific availability changes.
 * @property rating The average rating of the doctor.
 * @property reviews List of reviews for the doctor.
 * @property fcmToken Firebase Cloud Messaging token for notifications.
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
    var reviews:List<Review> = emptyList(),
    var fcmToken: String = ""

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


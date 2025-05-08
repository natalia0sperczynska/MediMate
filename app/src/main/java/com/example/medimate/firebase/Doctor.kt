package com.example.medimate.firebase

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
    val id: String = "",
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profilePicture: String = "",
    val specialisation:String = "",
    val room:String = "",
    val availability:Availability = Availability(
        monday = generateTimeSlots(),
        tuesday = generateTimeSlots(),
        wednesday = generateTimeSlots(),
        thursday = generateTimeSlots(),
        friday = generateTimeSlots(),
        saturday = generateTimeSlots(),
        sunday = generateTimeSlots()
    ),
    var availabilityChanges : List<String> = emptyList()

){
    fun doesMatchSearchQuery(query: String):Boolean{
        val matchingCombinations = listOf(
            "$name$surname",
            "$name $surname",
            "${name.first()}${surname.first()}"
        )
        return matchingCombinations.any{
            it.contains(query, ignoreCase = true)
        }
    }
    companion object{

        fun generateTimeSlots(): List<Term> {
            val timeSlots = mutableListOf<Term>()
            var hour=10
            var minute=0

            while (hour < 18) {
                val startTime = String.format("%02d:%02d", hour, minute)
                minute += 30
                if (minute == 60) {
                    hour ++
                    minute = 0
                }
                val endTime = String.format("%02d:%02d", hour, minute)
                timeSlots.add(Term(startTime, endTime))
            }
            return timeSlots
        }
    }
}


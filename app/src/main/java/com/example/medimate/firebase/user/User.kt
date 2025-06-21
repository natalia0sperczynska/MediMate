package com.example.medimate.firebase.user

import com.example.medimate.firebase.appointment.Appointment

/**
 * Data class representing a User.
 * Contains information such as personal details, contact information, and medical history.
 *
 * @property id Unique identifier for the user.
 * @property name The user's first name.
 * @property surname The user's last name.
 * @property email The user's email address.
 * @property dateOfBirth The user's date of birth.
 * @property phoneNumber The user's phone number.
 * @property profilePictureUrl URL to the user's profile picture.
 * @property address A map containing the user's address details (street, city, etc.).
 * @property allergies A list of the user's known allergies.
 * @property diseases A list of the user's medical conditions.
 * @property medications A list of the user's prescribed medications.
 */
data class User (
    var id: String = "",
    var name: String? = null,
    var surname: String?=null,
    var email: String = "",
    var dateOfBirth: String = "",
    var phoneNumber: String = "",
    var profilePictureUrl: String = "",
    var address:List<String> = emptyList(),
    var allergies:List<String> = emptyList(),
    var diseases:List<String> = emptyList(),
    var medications:List<String> = emptyList(),
    var documents:List<String> = emptyList(),
   // var medicalHistory:List<Appointment> = listOf(),
){
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$name$surname",
            "$name $surname",
            "${name!!.first()}${surname!!.first()}"
        )
        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }

    companion object {
        /**
         * Function creates a User instance from a map of field names to values.
         * This is useful for retrieving user data from Firestore.
         *
         * @param data A map containing user data.
         * @return A User object populated with data from the map.
         */
        fun fromMap(data: Map<String, Any?>): User {
            return User(

                id = data["id"] as? String ?: "",

                name = data["name"] as? String,

                surname = data["surname"] as? String,

                email = data["email"] as? String ?: "",
                dateOfBirth = data["dateOfBirth"] as? String ?: "",

                phoneNumber = data["phoneNumber"] as? String ?: "",

                profilePictureUrl = data["profilePictureUrl"] as? String ?: "",

                address = (data["address"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),

                allergies = (data["allergies"] as? List<*>)
                    ?.mapNotNull { it as? String } ?: listOf(),
                diseases = (data["diseases"] as? List<*>)
                    ?.mapNotNull { it as? String } ?: listOf(),
                medications = (data["medications"] as? List<*>)
                    ?.mapNotNull { it as? String } ?: listOf(),
                documents = (data["documents"] as? List<*>)
                    ?.mapNotNull { it as? String } ?: listOf(),

            )
        }

    }
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "surname" to surname,
            "email" to email,
            "dateOfBirth" to dateOfBirth,
            "phoneNumber" to phoneNumber,
            "profilePictureUrl" to profilePictureUrl,
            "address" to address,
            "allergies" to allergies,
            "diseases" to diseases,
            "medications" to medications,
            "documents" to documents
        )
    }
}
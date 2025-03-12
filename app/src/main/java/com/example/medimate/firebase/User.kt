package com.example.medimate.firebase

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
    val id: String = "",
    val name: String? = null,
    val surname: String?=null,
    val email: String = "",
    val dateOfBirth: String = "",
    val phoneNumber: String = "",
    val profilePictureUrl: String = "",
    val address:Map<String,String> = mapOf(),
    val allergies:List<String> = listOf(),
    val diseases:List<String> = listOf(),
    val medications:List<String> = listOf()
){

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

                phoneNumber = data["phoneNumber"] as? String ?: "",

                dateOfBirth = data["dateOfBirth"] as? String ?: "",

                address = (data["address"] as? Map<*, *>)
                    ?.mapNotNull { (key, value) ->
                        if (key is String && value is String) key to value else null
                    }?.toMap() ?: mapOf(),

                allergies = (data["allergies"] as? List<*>)
                    ?.mapNotNull { it as? String } ?: listOf(),
                diseases = (data["diseases"] as? List<*>)
                    ?.mapNotNull { it as? String } ?: listOf(),
                medications = (data["medications"] as? List<*>)
                    ?.mapNotNull { it as? String } ?: listOf(),

                profilePictureUrl = data["profilePictureUrl"] as? String ?: ""
            )
        }
    }
}
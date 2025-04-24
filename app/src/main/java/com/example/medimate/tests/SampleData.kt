package com.example.medimate.tests

import android.annotation.SuppressLint
import com.example.medimate.firebase.Appointment
import com.example.medimate.firebase.Availability
import com.example.medimate.firebase.Doctor
import com.example.medimate.firebase.Doctor.Companion.generateTimeSlots
import com.example.medimate.firebase.Status
import com.example.medimate.firebase.Term
import com.example.medimate.firebase.User

fun getSampleAppointments(): List<Appointment> {
    val doctor = Doctor(
        id = "doc001",
        name = "Arnold",
        surname = "Schwarzenegger",
        email = "arnold@doctor.pl",
        phoneNumber = "123456789",
        profilePicture = "https://example.com/arnold.jpg",
        specialisation = "Cardiology",
        room = "101"
    )

    val patient1 = User(
        id = "pat001",
        name = "Alice",
        surname = "Johnson",
        email = "alice@example.com",
        phoneNumber = "987654321",
        dateOfBirth = "1990-04-12",
        profilePictureUrl = "https://example.com/alice.jpg",
        address = mapOf("street" to "Main St", "city" to "Warsaw"),
        allergies = listOf("Penicillin"),
        diseases = listOf("Hypertension"),
        medications = listOf("Atenolol")
    )

    val patient2 = User(
        id = "pat002",
        name = "Bob",
        surname = "Smith",
        email = "bob@example.com",
        phoneNumber = "876543210",
        dateOfBirth = "1985-09-27",
        profilePictureUrl = "https://example.com/bob.jpg",
        address = mapOf("street" to "Oak Rd", "city" to "Krakow"),
        allergies = listOf("None"),
        diseases = listOf("Asthma"),
        medications = listOf("Ventolin")
    )

    return listOf(
        Appointment(
            id = "appt001",
            doctor = doctor,
            patient = patient1,
            date = "2025-04-12 10:30",
            status = Status.EXPECTED,
            diagnosis = "",
            notes = "Patient reported chest pain",
            url = "https://meet.example.com/arnold-alice"
        ),
        Appointment(
            id = "appt002",
            doctor = doctor,
            patient = patient2,
            date = "2025-04-13 14:00",
            status = Status.COMPLETED,
            diagnosis = "Asthma attack",
            notes = "Prescribed new inhaler dosage",
            url = "https://meet.example.com/arnold-bob"
        )
    )
}
fun getSampleDoctors(): List<Doctor> {
    return listOf(
        Doctor(
            id = "doc1",
            name = "Test",
            surname = "Doc",
            email = "doc@clinic.com",
            phoneNumber = "123456789",
            profilePicture = "",
            specialisation = "Cardiologist",
            room = "6",
            availability = Availability(
                monday = listOf(
                    Term(startTime = "10:00", endTime = "10:30",isAvailable = true),
                    Term(startTime = "10:30", endTime = "11:00",isAvailable = true)
                ),
                tuesday = listOf(
                    Term(startTime = "12:00", endTime = "12:30",isAvailable = true)
                ),
                wednesday = listOf(
                    Term(startTime = "10:00", endTime = "10:30",isAvailable = true),
                    Term(startTime = "10:30", endTime = "11:00",isAvailable = true)
                ),
                thursday = listOf(
                    Term(startTime = "10:00", endTime = "10:30",isAvailable = true),
                    Term(startTime = "10:30", endTime = "11:00",isAvailable = true)
                ),
                friday = listOf(
                    Term(startTime = "10:00", endTime = "10:30",isAvailable = true),
                    Term(startTime = "10:30", endTime = "11:00",isAvailable = true)
                ),
                saturday = listOf(
                    Term(startTime = "10:00", endTime = "10:30",isAvailable = true),
                    Term(startTime = "10:30", endTime = "11:00",isAvailable = true)
                ),
                sunday = listOf(
                    Term(startTime = "10:00", endTime = "10:30",isAvailable = true),
                    Term(startTime = "10:30", endTime = "11:00",isAvailable = true)
                ),
            )
        ),
        Doctor(
            id = "1",
            name = "Anna",
            surname = "Kowalska",
            email = "anna.kowalska@example.com",
            phoneNumber = "+48123456789",
            profilePicture = "",
            specialisation = "Cardiologist",
            room = "101",
            availability = Availability(
                monday = generateTimeSlots(),
                tuesday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots(),
                friday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "2",
            name = "Jan",
            surname = "Nowak",
            email = "jan.nowak@example.com",
            phoneNumber = "+48987654321",
            profilePicture = "",
            specialisation = "Neurologist",
            room = "202",
            availability = Availability(
                monday = generateTimeSlots(),
                tuesday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                friday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "3",
            name = "Ewa",
            surname = "Wiśniewska",
            email = "ewa.wisniewska@example.com",
            phoneNumber = "+48777777777",
            profilePicture = "",
            specialisation = "Dermatologist",
            room = "303",
            availability = Availability(
                monday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "3",
            name = "Ewa",
            surname = "Wiśniewska",
            email = "ewa.wisniewska@example.com",
            phoneNumber = "+48777777777",
            profilePicture = "",
            specialisation = "Dermatologist",
            room = "303",
            availability = Availability(
                monday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "3",
            name = "Ewa",
            surname = "Wiśniewska",
            email = "ewa.wisniewska@example.com",
            phoneNumber = "+48777777777",
            profilePicture = "",
            specialisation = "Dermatologist",
            room = "303",
            availability = Availability(
                monday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "3",
            name = "Ewa",
            surname = "Wiśniewska",
            email = "ewa.wisniewska@example.com",
            phoneNumber = "+48777777777",
            profilePicture = "",
            specialisation = "Dermatologist",
            room = "303",
            availability = Availability(
                monday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "3",
            name = "Ewa",
            surname = "Wiśniewska",
            email = "ewa.wisniewska@example.com",
            phoneNumber = "+48777777777",
            profilePicture = "",
            specialisation = "Dermatologist",
            room = "303",
            availability = Availability(
                monday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots()
            )
        ),
        Doctor(
            id = "3",
            name = "Ewa",
            surname = "Wiśniewska",
            email = "ewa.wisniewska@example.com",
            phoneNumber = "+48777777777",
            profilePicture = "",
            specialisation = "Dermatologist",
            room = "303",
            availability = Availability(
                monday = generateTimeSlots(),
                wednesday = generateTimeSlots(),
                thursday = generateTimeSlots()
            )
        )

    )

}

fun getSapleAvilableTerms(doctor: Doctor, date: String): List<Term> {
    val dayOfWeek = java.time.LocalDate.parse(date, java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        .dayOfWeek
        .name
        .lowercase()

    return when (dayOfWeek) {
        "monday" -> doctor.availability.monday
        "tuesday" -> doctor.availability.tuesday
        "wednesday" -> doctor.availability.wednesday
        "thursday" -> doctor.availability.thursday
        "friday" -> doctor.availability.friday
        "saturday" -> doctor.availability.saturday
        "sunday" -> doctor.availability.sunday
        else -> listOf()
    }.filter { it.isAvailable }
}
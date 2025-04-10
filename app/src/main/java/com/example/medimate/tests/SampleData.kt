package com.example.medimate.tests

import com.example.medimate.firebase.Appointment
import com.example.medimate.firebase.Availability
import com.example.medimate.firebase.Doctor
import com.example.medimate.firebase.Doctor.Companion.generateTimeSlots
import com.example.medimate.firebase.Status
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


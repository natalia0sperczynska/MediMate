package com.example.medimate.firebase.doctor

import com.example.medimate.firebase.appointment.Appointment
import com.example.medimate.firebase.appointment.Status
import com.example.medimate.firebase.appointment.Term
import com.example.medimate.firebase.review.Review
import com.example.medimate.firebase.user.User
import com.example.medimate.user.appointments.getAvailableTermsForDate
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Class for interacting with Firebase Firestore for user data management.
 */
class DoctorDAO {
    /**
     * Function registers or updates a user in the Firestore database.
     * If a user with the same ID already exists, their document will be overwritten.
     *
     * @param user The user object to register or update.
     * @throws Exception If an error occurs while saving the user data.
     */

    suspend fun registerOrUpdateDoctor(doctor: Doctor) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            mFireStore.collection("doctors").document(doctor.id).set(doctor).await()
        } catch (e: Exception) {
            throw Exception("Error saving user data: ${e.message}")
        }
    }
    /**
     * Function loads doctors data from Firestore based on the given user ID.
     *
     * @param doctorId The ID of the doctor to fetch.
     * @return A map containing the doctor data, or null if the document does not exist.
     * @throws Exception If an error occurs while loading the doctor data.
     */
    suspend fun loadDoctorData(doctorId: String): Map<String, Any>? {
        val mFireStore = FirebaseFirestore.getInstance()
        try {

            val documentSnapshot = mFireStore.collection("doctors")
                .document(doctorId)
                .get()
                .await()
            return documentSnapshot.data
        } catch (e: Exception) {

            throw Exception("Error loading doctor data: ${e.message}")
        }
    }
    /**
     * Function updates doctors data in Firestore.
     * Only non-null and non-blank values are updated in the user's document.
     *
     * @param doctorId The ID of the doctor to update.
     * @param updatedData A map containing the updated data for the doctor.
     * @throws Exception If an error occurs while updating the doctor data.
     */
    suspend fun updateDoctorData(doctorId: String, updatedData: Map<String, Any?>) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {

            val filteredData = updatedData.filterValues { value ->
                value != null && !(value is String && value.isBlank())
            }

            if (filteredData.isEmpty()) return

            mFireStore.collection("doctors")
                .document(doctorId)
                .update(filteredData)
                .await()

        } catch (e: Exception) {
            throw Exception("Error updating user data: ${e.message}")
        }
    }
    /**
     * Retrieves a list of all doctors from Firestore.
     *
     * @return List of [Doctor] objects.
     * @throws Exception If an error occurs while fetching the data.
     */
    suspend fun getAllDoctors(): List<Doctor> {
        val mFireStore = FirebaseFirestore.getInstance()
        val doctorsList = mutableListOf<Doctor>()
        val result = mFireStore.collection("doctors").get().await()
        for (document in result) {
            val doctor = document.toObject(Doctor::class.java)
            doctorsList.add(doctor)
        }
        return doctorsList
    }
    /**
     * Loads all appointments for a specific doctor from Firestore.
     *
     * @param doctorId The ID of the doctor.
     * @return List of [Appointment] objects for the doctor.
     * @throws Exception If an error occurs while fetching the data.
     */
    suspend fun loadAppointments(doctorId: String): List<Appointment> {
        val mFireStore = FirebaseFirestore.getInstance()
        val appointmentsList = mutableListOf<Appointment>()
        val result = mFireStore.collection("appointments").get().await()
        for (document in result) {
            val appointment = document.toObject(Appointment::class.java)
            if (appointment.doctorId == doctorId)
                appointmentsList.add(appointment)
        }
        return appointmentsList

    }
    /**
     * Updates the availability of a doctor for a specific date with the given terms (without an appointment context).
     *
     * @param doctor The [Doctor] whose availability is being updated.
     * @param date The date for which availability is changed.
     * @param terms The list of available [Term]s for the date.
     * @throws Exception If an error occurs while updating the data.
     */
    suspend fun updateDoctorAvailabilityNotApp(doctor: Doctor, date: String, terms: List<Term>) {
        val mFireStore = FirebaseFirestore.getInstance()
        try {
            val updateChanges = doctor.availabilityChanges.toMutableMap().apply {
                put(date, terms)
            }

            mFireStore.collection("doctors").document(doctor.id)
                .update("availabilityChanges", updateChanges)
                .await()
        } catch (e: Exception) {
            throw Exception("Error updating availability: ${e.message}")
        }
    }
    /**
     * Updates the doctor's availability after an appointment is booked.
     *
     * @param doctor The [Doctor] whose availability is being updated.
     * @param appointment The [Appointment] that affects availability.
     */
    suspend fun updateDoctorAvailability(doctor: Doctor?,appointment: Appointment){
        if (doctor == null) return
        val mFireStore = FirebaseFirestore.getInstance()
        try{
            val date = appointment.date
            val time = appointment.time
            val updateChanges = doctor.availabilityChanges.toMutableMap()

            val currentTerms = updateChanges[date] ?:
            getAvailableTermsForDate(doctor, date).map { it.copy() }

            val updateTerms = currentTerms.map { term ->
                val termTime = "${term.startTime}-${term.endTime}"
                if (termTime==time){
                    term.copy(isAvailable = false)
                }else{
                    term
                }
            }
            updateChanges[date] = updateTerms

            mFireStore.collection("doctors").document(doctor.id)
                .update("availabilityChanges", updateChanges)
                .await()

            mFireStore.collection("doctors").document(doctor.id).update("availabilityChanges",updateChanges).await()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }


    suspend fun addAvailabilityChanGE(doctorId: String, newDate:String,time:String){
        val mFireStore = FirebaseFirestore.getInstance()
        val doc = mFireStore.collection("doctors").document(doctorId)
        //doc.update()
    }
    /**
     * Retrieves a [Doctor] object by ID from Firestore.
     *
     * @param id The ID of the doctor.
     * @return The [Doctor] object if found, null otherwise.
     */
    suspend fun getDoctorById(id: String?): Doctor? {
        if (id.isNullOrBlank()) return null

        return try {
            val documentSnapshot = FirebaseFirestore.getInstance()
                .collection("doctors")
                .document(id)
                .get()
                .await()

            documentSnapshot.toObject(Doctor::class.java)?.apply {
                this.id = documentSnapshot.id
                if (this.availabilityChanges == null) {
                    this.availabilityChanges = emptyMap()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    /**
     * Retrieves a list of patients for a given doctor based on their appointments.
     *
     * @param doctorId The ID of the doctor.
     * @return List of [User] objects who are patients of the doctor.
     * @throws Exception If an error occurs while loading patients.
     */
    suspend fun getPatientsForDoctor(doctorId: String): List<User> {
        val mFireStore = FirebaseFirestore.getInstance()
        val patientsList = mutableListOf<User>()

        try {
            val appointments = mFireStore.collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .await()

            val patientIds = appointments.documents
                .mapNotNull { it.toObject(Appointment::class.java)?.patientId }
                .distinct()

            if (patientIds.isNotEmpty()) {
                val patients = mFireStore.collection("users")
                    .whereIn(FieldPath.documentId(), patientIds)
                    .get()
                    .await()

                patients.documents.forEach { doc ->
                    doc.toObject(User::class.java)?.let { user ->
                        patientsList.add(user.copy(id = doc.id))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error loading patients: ${e.message}")
        }

        return patientsList
    }
    /**
     * Finds the closest upcoming appointment for a doctor.
     *
     * @param doctorId The ID of the doctor.
     * @return The closest [Appointment], or null if none found.
     */
    suspend fun getClosestAppointmentForDoctor(doctorId: String): Appointment? {
        val all_app = loadAppointments(doctorId)
        if (all_app.isEmpty()) {
            return null
        }
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val today = LocalDate.now()

        return all_app.mapNotNull { app ->
            try{
                val appointmentDate= LocalDate.parse(app.date,formatter)
                if(!appointmentDate.isBefore(today)&&app.status!= Status.CANCELLED){
                    Pair(app,appointmentDate)
                } else null
            } catch (e:Exception) {
                null
            }
        }
            .minByOrNull { (_, date) ->
                ChronoUnit.DAYS.between(today, date)
            }
            ?.first
    }
    /**
     * Retrieves the most frequent patients for a doctor, limited by the specified number.
     *
     * @param doctorId The ID of the doctor.
     * @param limit The maximum number of frequent patients to return (default is 5).
     * @return List of pairs containing [User] and their appointment count.
     * @throws Exception If an error occurs while loading frequent patients.
     */
    suspend fun getFrequentPatients(doctorId: String, limit: Int = 5): List<Pair<User, Int>> {
        val mFireStore = FirebaseFirestore.getInstance()
        val frequentPatients = mutableListOf<Pair<User, Int>>()

        try {
            val appointments = mFireStore.collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .await()
            val patientAppointmentCount = appointments.documents
                .mapNotNull { it.toObject(Appointment::class.java)?.patientId }
                .groupingBy { it }
                .eachCount()
            val topPatientIds = patientAppointmentCount.entries
                .sortedByDescending { it.value }
                .take(limit)
                .map { it.key }

            if (topPatientIds.isNotEmpty()) {
                val patients = mFireStore.collection("users")
                    .whereIn(FieldPath.documentId(), topPatientIds)
                    .get()
                    .await()
                patients.documents.forEach { doc ->
                    val user = doc.toObject(User::class.java)?.copy(id = doc.id)
                    user?.let {
                        val count = patientAppointmentCount[doc.id] ?: 0
                        frequentPatients.add(Pair(it, count))
                    }
                }
                frequentPatients.sortByDescending { it.second }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error loading frequent patients: ${e.message}")
        }

        return frequentPatients
    }
/**
 * Calculates and retrieves various statistics for a doctor, such as total appointments,
 * completed appointments, cancelled appointments, upcoming appointments, average rating,
 * total reviews, and monthly appointment stats for the last 6 months.
 *
 * @param doctorId The ID of the doctor.
 * @return A map containing statistics with keys such as "totalAppointments", "completedAppointments", etc.
 * @throws Exception If an error occurs while calculating statistics.
 */
    suspend fun getDoctorStatistics(doctorId: String): Map<String, Any> {
        val mFireStore = FirebaseFirestore.getInstance()
        val stats = mutableMapOf<String, Any>()

        try {
            val appointments = mFireStore.collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .await()

            val appointmentList = appointments.documents.mapNotNull {
                it.toObject(Appointment::class.java)
            }
            stats["totalAppointments"] = appointmentList.size
            stats["completedAppointments"] = appointmentList.count { it.status == Status.COMPLETED }
            stats["cancelledAppointments"] = appointmentList.count { it.status == Status.CANCELLED }
            stats["upcomingAppointments"] = appointmentList.count {
                it.status == Status.EXPECTED|| it.status == Status.EXPECTED
            }
            val reviews = mFireStore.collection("reviews")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .await()

            val reviewList = reviews.documents.mapNotNull {
                it.toObject(Review::class.java)
            }

            if (reviewList.isNotEmpty()) {
                val averageRating = reviewList.map { it.rate }.average()
                stats["averageRating"] = averageRating
                stats["totalReviews"] = reviewList.size
            } else {
                stats["averageRating"] = 0.0
                stats["totalReviews"] = 0
            }
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val now = LocalDate.now()
            val monthlyStats = mutableMapOf<String, Int>()

            for (i in 5 downTo 0) {
                val month = now.minusMonths(i.toLong())
                val monthKey = "${month.monthValue}/${month.year}"
                val monthStart = month.withDayOfMonth(1)
                val monthEnd = month.withDayOfMonth(month.lengthOfMonth())

                val count = appointmentList.count { app ->
                    try {
                        val appDate = LocalDate.parse(app.date, formatter)
                        !appDate.isBefore(monthStart) && !appDate.isAfter(monthEnd)
                    } catch (e: Exception) {
                        false
                    }
                }

                monthlyStats[monthKey] = count
            }

            stats["monthlyAppointments"] = monthlyStats

        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error calculating statistics: ${e.message}")
        }

        return stats
    }

}



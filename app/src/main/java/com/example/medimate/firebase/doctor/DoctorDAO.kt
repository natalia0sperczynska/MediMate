package com.example.medimate.firebase.doctor

import com.example.medimate.firebase.appointment.Appointment
import com.example.medimate.firebase.appointment.Status
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




}



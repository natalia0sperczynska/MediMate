package com.example.medimate.firebase.appointment
import androidx.annotation.Keep

@Keep
data class Appointment(
    var id: String = "",
    var doctorId: String="",
    val patientId: String ="",
    var date: String = "",
    var time : String ="",
    var status: Status = Status.EXPECTED,
    var diagnosis: String = "",
    var notes: String = "",
    var url: String = ""
)
{

fun toMap():Map<String,Any> {
    return mapOf(
        "id" to id,
        "doctorId" to doctorId,
        "patientId" to patientId,
        "date" to date,
        "time" to time,
        "status" to status.name,
        "diagnosis" to diagnosis,
        "notes" to notes,
        "url" to url
    )

}
}
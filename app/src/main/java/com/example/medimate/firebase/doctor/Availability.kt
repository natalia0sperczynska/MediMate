package com.example.medimate.firebase.doctor
import com.example.medimate.firebase.appointment.Term
import java.time.DayOfWeek

data class Availability (
    val monday: List<Term> = generateTimeSlots(),
    val tuesday: List<Term> = generateTimeSlots(),
    val wednesday: List<Term> = generateTimeSlots(),
    val thursday: List<Term> = generateTimeSlots(),
    val friday: List<Term> = generateTimeSlots(),
    val saturday:List<Term> = generateTimeSlots(),
    val sunday: List<Term> = generateTimeSlots()
) {

    fun getDefaultTermsForDay(dayOfWeek: DayOfWeek): List<Term> {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> monday
            DayOfWeek.TUESDAY -> tuesday
            DayOfWeek.WEDNESDAY -> wednesday
            DayOfWeek.THURSDAY -> thursday
            DayOfWeek.FRIDAY -> friday
            DayOfWeek.SATURDAY -> saturday
            DayOfWeek.SUNDAY -> sunday
        }
    }
    companion object{
        fun generateTimeSlots(): List<Term> {
            val timeSlots = mutableListOf<Term>()
            var hour=8
            var minute=0

            while (hour in 8..12 || hour in 14..16) {
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
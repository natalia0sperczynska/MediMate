package com.example.medimate.firebase
import java.util.Locale
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Availability (
    val monday: List<Term> = generateTimeSlots(),
    val tuesday: List<Term> = generateTimeSlots(),
    val wednesday: List<Term> = generateTimeSlots(),
    val thursday: List<Term> = generateTimeSlots(),
    val friday: List<Term> = generateTimeSlots(),
    val saturday:List<Term> = generateTimeSlots(),
    val sunday: List<Term> = generateTimeSlots()
) {
    fun getTermsForDay(date: String):List<Term> {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault())
        val localDate = LocalDate.parse(date, formatter)
        return when (localDate.dayOfWeek) {
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
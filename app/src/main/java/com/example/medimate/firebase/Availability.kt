package com.example.medimate.firebase

import java.util.Locale
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Availability (
    val monday: List<Term> = listOf(),
    val tuesday: List<Term> = listOf(),
    val wednesday: List<Term> = listOf(),
    val thursday: List<Term> = listOf(),
    val friday: List<Term> = listOf(),
    val saturday:List<Term> = listOf(),
    val sunday: List<Term> = listOf()
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


}
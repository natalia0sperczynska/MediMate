package com.example.medimate.firebase

data class Availability (
    val monday: List<Term> = listOf(),
    val tuesday: List<Term> = listOf(),
    val wednesday: List<Term> = listOf(),
    val thursday: List<Term> = listOf(),
    val friday: List<Term> = listOf(),
    val saturday:List<Term> = listOf(),
    val sunday: List<Term> = listOf()
)
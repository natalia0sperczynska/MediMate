package com.example.medimate.firebase.admin

/**
 * Data access object for admin-related Firestore operations.
 * Provides methods for CRUD operations on admin documents.
 */
class Admin (
    val id: String = "",
    val name: String? = null,
    val surname: String?=null,
    val email: String = "",
)
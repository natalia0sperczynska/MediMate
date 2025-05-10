package com.example.medimate.firebase

import com.google.firebase.auth.FirebaseAuth

object AuthManager{
    fun getCurrentUser():User?{
        val firebaseuser = FirebaseAuth.getInstance().currentUser
        return firebaseuser?.let {
            User(
                id = it.uid,
                name = it.displayName ?: "",
                email = it.email ?: ""
            )
        }
    }
}
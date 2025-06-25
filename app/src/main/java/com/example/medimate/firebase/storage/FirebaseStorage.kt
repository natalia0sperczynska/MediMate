package com.example.medimate.firebase.storage

import com.google.firebase.Firebase
import com.google.firebase.storage.storage

/**
 * Utility object providing access to Firebase Storage.
 */
var storage = Firebase.storage("gs://my-custom-bucket")
package com.example.medimate.firebase

import androidx.compose.runtime.Composable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.medimate.firebase.user.User
import com.example.medimate.firebase.user.UserDAO
import com.google.firebase.auth.FirebaseAuth
/**
 * Utility object for managing authentication state.
 * Provides access to the current authenticated user.
 */
object AuthManager{
    fun getCurrentUser(): User?{
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
/**
 * Repository object for managing current user data.
 * Maintains the current user state and provides access to user data.
 */
object UserRepository {
    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    suspend fun loadUserData(userId: String) {
        val user = UserDAO().getUserById(userId)
        _currentUser.value = user
    }

    fun getProfilePictureUrl(): String? {
        return _currentUser.value?.profilePictureUrl
    }
}
/**
 * Composable function that provides user data to child components.
 *
 * @param content The child composables that receive the user data.
 */
@Composable
fun UserProvider(
    content: @Composable (String?) -> Unit
) {
    val auth = AuthManager
    val userId = auth.getCurrentUser()?.id
    var profilePictureUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        userId?.let {
            UserRepository.loadUserData(it)
            profilePictureUrl = UserRepository.getProfilePictureUrl()
        }
    }

    content(profilePictureUrl)
}
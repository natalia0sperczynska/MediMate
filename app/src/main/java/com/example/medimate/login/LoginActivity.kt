package com.example.medimate.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.medimate.R
import com.example.medimate.mainViews.BaseActivity
import com.example.medimate.mainViews.MainAdmin
import com.example.medimate.mainViews.MainDoctor
import com.example.medimate.mainViews.MainUser
import com.example.medimate.register.DataEntryActivity
import com.google.firebase.auth.FirebaseAuth
/**
 * Activity for handling user login.
 * Allows the user to log in using their email and password.
 * Provides a link to the registration activity if the user doesn't have an account.
 */
class LoginActivity : BaseActivity() {

    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var loginButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inputEmail = findViewById(R.id.editTextTextEmailAddressLogin)
        inputPassword = findViewById(R.id.editTextTextPasswordLogin)
        loginButton = findViewById(R.id.loginButton)

        loginButton?.setOnClickListener {
            logInRegisteredUser()
        }

        val goToRegisterActivityButton = findViewById<Button>(R.id.dontHaveAccountButton)
        goToRegisterActivityButton.setOnClickListener {
            val intent = Intent(this, DataEntryActivity::class.java)
            startActivity(intent)
        }
    }
    /**
     * Function validates the login details entered by the user.
     *
     * @return True if the email and password are valid, false otherwise.
     */
    private fun validateLoginDetails(): Boolean {
        val email = inputEmail?.text.toString().trim { it <= ' ' }
        val password = inputPassword?.text.toString().trim { it <= ' ' }

        return when {
            email.isEmpty() -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            password.isEmpty() -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            else -> {
                true
            }
        }
    }
    /**
     * Attempts to log in the user using Firebase Authentication.
     * If successful, the user is redirected to the main user activity.
     */
    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            val email = inputEmail?.text.toString().trim { it <= ' ' }
            val password = inputPassword?.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful&&email.contains("@doc", ignoreCase = true)) {
                        showErrorSnackBar("You are logged in successfully.", false)
                        goToMainDoctor()
                    }
                    else if (task.isSuccessful&&email.contains("@admin", ignoreCase = true)) {
                        showErrorSnackBar("You are logged in successfully.", false)
                        goToMainAdmin()
                    }
                    else if (task.isSuccessful) {
                        showErrorSnackBar("You are logged in successfully.", false)
                        goToMainUser()
                    }
                    else {
                        showErrorSnackBar(task.exception?.message.toString(), true)
                    }
                }
        }
    }
    /**
     * Navigates to the main user activity after successful login.
     */
    private fun goToMainUser() {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email.orEmpty()

        val intent = Intent(this, MainUser::class.java).apply {
            putExtra("uID", email)
        }
        startActivity(intent)
    }
    /**
     * Navigates to the main doctor activity after successful login.
     */
    private fun goToMainDoctor() {
        val doctor = FirebaseAuth.getInstance().currentUser
        val email = doctor?.email.orEmpty()

        val intent = Intent(this, MainDoctor::class.java).apply {
            putExtra("dID", email)
        }
        startActivity(intent)
    }
    /**
     * Navigates to the main admin activity after successful login.
     */
    private fun goToMainAdmin() {
        val admin = FirebaseAuth.getInstance().currentUser
        val email = admin?.email.orEmpty()

        val intent = Intent(this, MainAdmin::class.java).apply {
            putExtra("aID", email)
        }
        startActivity(intent)
    }
}

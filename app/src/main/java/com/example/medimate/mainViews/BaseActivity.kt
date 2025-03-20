package com.example.medimate.mainViews

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
//import com.example.medimate.R
import com.google.android.material.snackbar.Snackbar
/**
 * Base activity class providing a utility method for showing SnackBars with custom error/success messages.
 */
open class BaseActivity : AppCompatActivity() {
    /**
     * Displays a SnackBar with a message and appropriate color based on whether it's an error or not.
     *
     * @param message The message to display.
     * @param errorMessage True if it's an error message, false if it's a success message.
     */
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                if (errorMessage) R.color.colorSnackBarError else R.color.colorSnackBarSuccess
            )
        )
        snackbar.show()
    }
}
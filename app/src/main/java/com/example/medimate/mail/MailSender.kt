package com.example.medimate.mail

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Sends an email asynchronously using SMTP via Gmail's SMTP server.
 *
 * This function performs the email sending operation on the IO dispatcher.
 * It connects to Gmail SMTP with authentication and TLS enabled.
 *
 * @param context The context from which this is called (used if needed for future extensions).
 * @param recipient The email address of the recipient.
 * @param subject The subject line of the email.
 * @param body The plain text body content of the email.
 * @return `true` if the email was sent successfully, `false` if an error occurred.
 *
 * @throws MessagingException or other exceptions are caught internally and logged.
 */
suspend fun sendMail(
    context: Context,
    recipient: String,
    subject: String,
    body: String
): Boolean = withContext(Dispatchers.IO) {
    try {
        val props = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.ssl.trust", "smtp.gmail.com")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(
                    "medimateeclinic@gmail.com",
                    "vblvjumjuxtkeihn"
                )
            }
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress("medimateeclinic@gmail.com"))
            addRecipient(Message.RecipientType.TO, InternetAddress(recipient))
            setSubject(subject)
            setText(body)
        }

        Transport.send(message)
        true
    } catch (e: Exception) {
        Log.e("EmailError", "Email sending failed", e)
        false
    }
}

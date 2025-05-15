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
                    "vblvjumjuxtkeihn" // Use app-specific password (not regular one!)
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

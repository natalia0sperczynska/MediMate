package com.example.medimate.mail

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

suspend fun sendMail(context: Context, recipient: String): Boolean = withContext(Dispatchers.IO) {
    return@withContext try {
        val props = Properties().apply {
            put("mail.smtp.host", "live.smtp.mailtrap.io") // Same host but now sends real emails
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication("api", "your_production_api_token") // Different token!
        })

        MimeMessage(session).apply {
            setFrom(InternetAddress("noreply@yourdomain.com")) // Must be your verified domain
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient))
            subject = "Important message from MediMate"
            setText("Hello from our app!")
        }.let { Transport.send(it) }

        true
    } catch (e: Exception) {
        Log.e("EmailSender", "Error sending real email", e)
        false
    }
}
package com.example.medimate.mail

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

suspend fun sendMail(context: Context): Boolean = withContext(Dispatchers.IO) {
    return@withContext try {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.e("EmailSender", "No network connection")
            false
        } else {
            val props = Properties().apply {
                put("mail.smtp.host", "live.smtp.mailtrap.io")
                put("mail.smtp.port", "587")
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.ssl.trust", "live.smtp.mailtrap.io")
                put("mail.smtp.connectiontimeout", "10000")
                put("mail.smtp.timeout", "10000")
                put("mail.smtp.localhost", "android.local")
            }

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication() =
                    PasswordAuthentication("api", "378bea846f5253e27d48db583fae6604")
            })

            session.debug = true

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress("medimate@demomailtrap.co"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse("sperczynskanatalia@gmail.com"))
                subject = "Test from MediMate"
                setText("MediMate is sending greetings ! Register into our app please :(((((")
                setHeader("Message-ID", "<${UUID.randomUUID()}@android.local>")
            }

            Transport.send(message)
            true
        }
    } catch (e: Exception) {
        Log.e("EmailSender", "Error sending email", e)
        false
    }
}
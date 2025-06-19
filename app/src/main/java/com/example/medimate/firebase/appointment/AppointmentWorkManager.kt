package com.example.medimate.firebase.appointment

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class AppointmentWorkManager(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            AppointmentDAO().checkAndUpdatePastAppointments()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

object AppointmentWorkScheduler {
    fun scheduleAppointmentCheck(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<AppointmentWorkManager>(
            1, TimeUnit.DAYS
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "appointmentCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
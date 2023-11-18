package com.udacity.asteroidradar

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.repository.AsteroidRepository
import java.util.concurrent.TimeUnit

class AsteroidsWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getDatabase(applicationContext)
        val apiService = AsteroidApi.retrofitService
        val asteroidRepository = AsteroidRepository(apiService, database)

        return try {
            asteroidRepository.refreshAsteroids(getToday(), getFinalDate())
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }

    }

    companion object {
        const val WORK_NAME = "AsteroidsWorker"

        fun setupPeriodicWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(true)
                .build()

            val periodicRequest = PeriodicWorkRequestBuilder<AsteroidsWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        }
    }

}
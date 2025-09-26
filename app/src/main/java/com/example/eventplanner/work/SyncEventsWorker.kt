/**
 * Background worker for periodic event synchronization.
 * Syncs local database with remote API.
 * Scheduled via WorkManager in MainActivity.
 */

package com.example.eventplanner.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eventplanner.data.EventDatabase
import com.example.eventplanner.data.EventRepository
import com.example.eventplanner.data.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncEventsWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val dao = EventDatabase.getDatabase(context).eventDao()
    private val api = RetrofitInstance.api
    private val repository = EventRepository(dao, api)

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Trigger repository flow to fetch and sync events
                repository.getEvents().collect { /* Collect to ensure fetch and local update */ }
                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.retry()
            }
        }
    }
}
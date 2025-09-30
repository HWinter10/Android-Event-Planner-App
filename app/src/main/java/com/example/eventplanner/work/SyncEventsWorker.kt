/** Background worker to sync events from API to local database
* Uses CoroutineWorker & EventRepository
* Logs progress, success, & errors
*/
package com.example.eventplanner.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eventplanner.data.EventDatabase
import com.example.eventplanner.data.EventRepository
import com.example.eventplanner.data.RetrofitInstance
import com.example.eventplanner.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SyncEventsWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    // dependencies
    private val dao = EventDatabase.getDatabase(context).eventDao()
    private val api = RetrofitInstance.api
    private val repository = EventRepository(dao, api)

    override suspend fun doWork(): Result {
        Log.d("SyncEventsWorker", "Background sync started")

        return withContext(Dispatchers.IO) {
            try { // fetch events from API
                Log.d("SyncEventsWorker", "Fetching events from API...")
                repository.getEvents().collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            Log.d("SyncEventsWorker", "Loading events...")
                        }
                        is Resource.Success -> {
                            Log.d(
                                "SyncEventsWorker",
                                "Fetched ${resource.data.size} events from API"
                            )
                        }
                        is Resource.Error -> {
                            Log.e("SyncEventsWorker", "Error fetching events: ${resource.message}")
                        }
                    }
                }

                // verify local DB contents
                val localEvents = dao.getAllEvents().first()
                Log.d("SyncEventsWorker", "Local database has ${localEvents.size} events after sync")

                Log.d("SyncEventsWorker", "Background sync completed successfully")
                Result.success()
            } catch (e: Exception) {
                Log.e("SyncEventsWorker", "Background sync failed", e)
                Result.retry()
            }
        }
    }
}
/**
 * Entry point for app
 * Sets up Compose theme and content.
 * Initializes EventViewModel with EventViewModelFactory.
 * Hosts NavGraph for navigation.
 * Schedules periodic background sync via WorkManager & SyncEventsWorker.
*/
package com.example.eventplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.eventplanner.ui.theme.EventPlannerTheme
import com.example.eventplanner.viewmodel.EventViewModel
import com.example.eventplanner.viewmodel.EventViewModelFactory
import com.example.eventplanner.work.SyncEventsWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Schedule periodic background sync
        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncEventsWorker>(
            15, TimeUnit.MINUTES // Minimum allowed interval
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sync_events",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )

        // Set Compose content
        setContent {
            EventPlannerTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    eventViewModel = eventViewModel
                )
            }
        }
    }
}
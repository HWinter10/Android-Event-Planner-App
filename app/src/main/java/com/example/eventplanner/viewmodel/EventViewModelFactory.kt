/**
 * Factory to provide EventViewModel with repository dependency.
 * Required for viewModel initialization in Compose and Activity.
 */

package com.example.eventplanner.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eventplanner.data.*

class EventViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            // Get DAO from Room database
            val dao = EventDatabase.getDatabase(application).eventDao()
            // Get Retrofit API instance
            val api = RetrofitInstance.api
            // Create repository
            val repository = EventRepository(dao, api)
            // Return ViewModel instance
            return EventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

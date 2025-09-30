/**
 * Business logic for event operations.
 * Maintains StateFlow<Resource<List<EventEntity>>> for UI consumption.
 * Handles add, update, delete operations.
 * Communicates with EventRepository.
 */
package com.example.eventplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventplanner.data.EventEntity
import com.example.eventplanner.data.EventRepository
import com.example.eventplanner.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {
    // events list
    private val _events = MutableStateFlow<Resource<List<EventEntity>>>(Resource.Loading())
    val events: StateFlow<Resource<List<EventEntity>>> = _events.asStateFlow()
    // add event operation
    private val _addEventState = MutableStateFlow<Resource<Unit>?>(null)
    val addEventState: StateFlow<Resource<Unit>?> = _addEventState.asStateFlow()

    init {
        loadEvents()
    }
    // load all events
    private fun loadEvents() {
        viewModelScope.launch {
            repository.getEvents()
                .catch { e ->
                    _events.value = Resource.Error(e.localizedMessage ?: "Unknown error")
                }
                .collect { resource ->
                    _events.value = resource
                }
        }
    }
    // add new event
    fun addEvent(
        title: String,
        description: String,
        date: String,
        time: String,
        location: String,
        reminder: Boolean,
        attendees: List<String>
    ) {
        viewModelScope.launch {
            _addEventState.value = Resource.Loading()
            val event = EventEntity(
                title = title,
                description = description,
                date = date,
                time = time,
                location = location,
                reminder = reminder,
                attendees = attendees
            )
            try {
                repository.addEvent(event)
                _addEventState.value = Resource.Success(Unit)
                loadEvents()
            } catch (e: Exception) {
                _addEventState.value = Resource.Error(e.localizedMessage ?: "Failed to add event")
            }
        }
    }
    // reset add event
    fun resetAddEventState() {
        _addEventState.value = null
    }
    // update existing event
    fun updateEvent(event: EventEntity) {
        viewModelScope.launch {
            try {
                repository.updateEvent(event)
                loadEvents()
            } catch (e: Exception) {
                _events.value = Resource.Error(e.localizedMessage ?: "Failed to update event")
            }
        }
    }
    // delete existing event
    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch {
            try {
                repository.deleteEvent(event)
                loadEvents()
            } catch (e: Exception) {
                _events.value = Resource.Error(e.localizedMessage ?: "Failed to delete event")
            }
        }
    }
}
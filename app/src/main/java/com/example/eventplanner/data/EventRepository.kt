/**
 * Repository for events.
 * Abstracts data sources (local database, optional network).
 * Provides suspend functions to fetch, add, update, delete events.
 */

package com.example.eventplanner.data

import com.example.eventplanner.network.EventApi
import com.example.eventplanner.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import java.io.IOException

class EventRepository(
    private val dao: EventDao,
    private val api: EventApi
) {

    // Exposed Flow for UI
    fun getEvents(): Flow<Resource<List<EventEntity>>> = flow {
        emit(Resource.Loading())

        try {
            // First, emit local data for offline support
            val localEvents: List<EventEntity> = try {
                dao.getAllEvents().first()
            } catch (_: NoSuchElementException) {
                emptyList()
            }
            emit(Resource.Success(localEvents))

            // Then fetch from network
            val remoteDtos = api.getEvents()
            val remoteEntities = remoteDtos.map { it.toEntity() }

            // Save/update local cache
            remoteEntities.forEach { dao.insertEvent(it) }

            // Emit updated list
            val updatedEvents: List<EventEntity> = dao.getAllEvents().first()
            emit(Resource.Success(updatedEvents))

        } catch (e: IOException) {
            // Network error, emit local data if available
            val localEvents: List<EventEntity> = try {
                dao.getAllEvents().first()
            } catch (_: NoSuchElementException) {
                emptyList()
            }
            emit(Resource.Success(localEvents))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    suspend fun addEvent(event: EventEntity) {
        // Add locally first
        dao.insertEvent(event)
        try {
            api.addEvent(event.toDto())
        } catch (e: Exception) {
            // Could queue for later retry
        }
    }

    suspend fun updateEvent(event: EventEntity) {
        dao.updateEvent(event)
        try {
            api.updateEvent(event.id, event.toDto())
        } catch (e: Exception) {
            // Could queue for later retry
        }
    }

    suspend fun deleteEvent(event: EventEntity) {
        dao.deleteEvent(event)
        try {
            api.deleteEvent(event.id)
        } catch (e: Exception) {
            // Could queue for later retry
        }
    }
}
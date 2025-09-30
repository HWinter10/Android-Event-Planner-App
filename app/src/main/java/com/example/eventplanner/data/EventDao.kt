/**
 * Room DAO for EventEntity.
 * Provides CRUD operations for events in the local database.
 */
package com.example.eventplanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    // get all events ordered by date & time
    @Query("SELECT * FROM events ORDER BY date, time")
    fun getAllEvents(): Flow<List<EventEntity>>
    // insert event, replace if conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)
    // update existing event
    @Update
    suspend fun updateEvent(event: EventEntity)
    // delete event
    @Delete
    suspend fun deleteEvent(event: EventEntity)
    // get single event by id, return null if not found
    @Query("SELECT * FROM events WHERE id = :eventId LIMIT 1")
    suspend fun getEventById(eventId: Int): EventEntity?
}
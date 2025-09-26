/**
 * Room DAO for EventEntity.
 * Provides CRUD operations for events in the local database.
 */

package com.example.eventplanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY date, time")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT * FROM events WHERE id = :eventId LIMIT 1")
    suspend fun getEventById(eventId: Int): EventEntity?
}
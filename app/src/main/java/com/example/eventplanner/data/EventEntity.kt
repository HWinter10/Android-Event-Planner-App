/**
 * Data class representing an event in local database.
 * Fields: id, title, description, date, time, location, reminder, attendees.
 */

package com.example.eventplanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "events")
@TypeConverters(Converters::class)
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: String,         // Format: YYYY-MM-DD
    val time: String,         // Format: HH:mm or hh:mm a
    val location: String,
    val reminder: Boolean,
    val attendees: List<String>
)
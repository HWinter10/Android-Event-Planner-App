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
    // primary key, auto generates
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,          // event title
    val description: String,    // event description
    val date: String,           // date format: YYYY-MM-DD
    val time: String,           // time format: HH:mm or hh:mm a
    val location: String,       // event location
    val reminder: Boolean,      // event reminder
    val attendees: List<String> // attendees
)
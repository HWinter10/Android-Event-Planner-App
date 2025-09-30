/**
 * Data Transfer Object for events from network.
 * Mapped to/from EventEntity via Mapper.
 */
package com.example.eventplanner.network

data class EventDto(
    val id: Int = 0,
    val title: String,
    val description: String,
    val date: String,       // YYYY-MM-DD
    val time: String,       // HH:mm or hh:mm a
    val location: String,
    val reminder: Boolean,
    val attendees: List<String>
)


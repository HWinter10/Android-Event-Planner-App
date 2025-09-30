/**
 * Maps between network DTOs (EventDto) and local entities (EventEntity).
 */
package com.example.eventplanner.data

import com.example.eventplanner.network.EventDto
// convert EventEntity to EventDto
fun EventEntity.toDto(): EventDto {
    return EventDto(
        id = this.id,
        title = this.title,
        description = this.description,
        date = this.date,
        time = this.time,
        location = this.location,
        reminder = this.reminder,
        attendees = this.attendees
    )
}
// convert EventDto to EventEntity
fun EventDto.toEntity(): EventEntity {
    return EventEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        date = this.date,
        time = this.time,
        location = this.location,
        reminder = this.reminder,
        attendees = this.attendees
    )
}
/**
 * Retrofit API interface for remote event operations.
 * Defines endpoints for fetching, adding, updating, and deleting events.
 */
package com.example.eventplanner.network

import retrofit2.http.*

interface EventApi {
    // fetch all events
    @GET("events")
    suspend fun getEvents(): List<EventDto>
    // fetch single event by id
    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: Int): EventDto
    // add new event
    @POST("events")
    suspend fun addEvent(@Body event: EventDto): EventDto
    // update existing event by id
    @PUT("events/{id}")
    suspend fun updateEvent(@Path("id") id: Int, @Body event: EventDto): EventDto
    // delete event by id
    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: Int)
}
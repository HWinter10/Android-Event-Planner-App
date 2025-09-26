/**
 * Retrofit API interface for remote event operations.
 * Defines endpoints for fetching, adding, updating, and deleting events.
 */

package com.example.eventplanner.network

import retrofit2.http.*

interface EventApi {

    @GET("events")
    suspend fun getEvents(): List<EventDto>

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: Int): EventDto

    @POST("events")
    suspend fun addEvent(@Body event: EventDto): EventDto

    @PUT("events/{id}")
    suspend fun updateEvent(@Path("id") id: Int, @Body event: EventDto): EventDto

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: Int)
}
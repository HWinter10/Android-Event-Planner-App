/**
 * Configures Retrofit instance for network operations.
 * Provides implementation of EventApi.
 */

package com.example.eventplanner.data

import com.example.eventplanner.network.EventApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://yourserver.com/api/" // Replace with actual API base URL when applicable

    val api: EventApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventApi::class.java)
    }
}

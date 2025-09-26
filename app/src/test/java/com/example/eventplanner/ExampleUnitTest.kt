package com.example.eventplanner.repository

import android.content.Context
import com.example.eventplanner.data.EventDao
import com.example.eventplanner.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class EventRepositoryTest {

    private lateinit var mockDao: EventDao
    private lateinit var mockApi: EventApi
    private lateinit var mockContext: Context
    private lateinit var repository: EventRepository

    private val testEvent = EventEntity(
        id = 1,
        title = "Test Event",
        description = "Desc",
        date = "2025-09-25",
        time = "12:00",
        location = "Test Location",
        attendees = listOf("Alice"),
        reminder = true
    )

    @Before
    fun setup() {
        mockDao = mock(EventDao::class.java)
        mockApi = mock(EventApi::class.java)
        mockContext = mock(Context::class.java)
        repository = EventRepository(mockDao, mockApi, mockContext)
    }

    @Test
    fun `getEvents emits success when API returns events`() = runBlocking {
        val response = Response.success(listOf(testEvent))
        `when`(mockApi.getEvents().execute()).thenReturn(response)
        `when`(mockDao.getAllEvents()).thenReturn(listOf())

        val flow = repository.getEvents()
        flow.collect { result ->
            assertTrue(result is Resource.Success)
            assertEquals(listOf(testEvent), result.data)
        }
        verify(mockDao).insertEvent(testEvent)
    }

    @Test
    fun `getEvents emits error when API fails`() = runBlocking {
        val response = Response.error<List<EventEntity>>(
            500,
            ResponseBody.create("application/json".toMediaTypeOrNull(), "")
        )
        `when`(mockApi.getEvents().execute()).thenReturn(response)

        val flow = repository.getEvents()
        flow.collect { result ->
            assertTrue(result is Resource.Error)
            assertTrue(result.message!!.contains("API Error"))
        }
    }

    @Test
    fun `getEvents falls back to local when network fails`() = runBlocking {
        `when`(mockApi.getEvents().execute()).thenThrow(IOException())
        `when`(mockDao.getAllEvents()).thenReturn(listOf(testEvent))

        val flow = repository.getEvents()
        flow.collect { result ->
            assertTrue(result is Resource.Success)
            assertEquals(listOf(testEvent), result.data)
        }
    }
}
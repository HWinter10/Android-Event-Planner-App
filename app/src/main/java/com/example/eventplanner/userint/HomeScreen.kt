/**
 * Displays list of events to the user.
 * Handles loading, error, and empty states.
 * Allows adding, editing, and deleting events.
 * Navigates using NavController.
 */
package com.example.eventplanner.userint

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventplanner.R
import com.example.eventplanner.Screen
import com.example.eventplanner.data.EventEntity
import com.example.eventplanner.util.Resource
import com.example.eventplanner.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    navController: NavController,
    eventViewModel: EventViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val eventsResource by eventViewModel.events.collectAsState()
    var deletingEventId by remember { mutableStateOf<Int?>(null) }
    var deleteError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.event_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 10.dp)
                        )
                        Text("Event Planner")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddEvent.route) }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val resource = eventsResource) {
                is Resource.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${resource.message}", color = MaterialTheme.colors.error)
                    }
                }
                is Resource.Success -> {
                    val events = resource.data
                    if (events.isNullOrEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No events yet. Tap + to add one.")
                        }
                    } else {
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(events) { event ->
                                EventCard(
                                    event = event,
                                    navController = navController,
                                    eventViewModel = eventViewModel,
                                    deletingEventId = deletingEventId,
                                    onDeletingChange = { deletingEventId = it },
                                    deleteError = deleteError,
                                    onDeleteError = { deleteError = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(eventsResource) {
        if (eventsResource is Resource.Success || eventsResource is Resource.Error) {
            deletingEventId = null
            if (eventsResource is Resource.Error)
                deleteError = (eventsResource as Resource.Error).message
        }
    }
}

@Composable
private fun EventCard(
    event: EventEntity,
    navController: NavController,
    eventViewModel: EventViewModel,
    deletingEventId: Int?,
    onDeletingChange: (Int?) -> Unit,
    deleteError: String?,
    onDeleteError: (String?) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable { navController.navigate(Screen.EditEvent.createRoute(event.id)) }
        ) {
            Text(event.title, style = MaterialTheme.typography.h6)
            Text(event.description, style = MaterialTheme.typography.body2)
            Spacer(Modifier.height(4.dp))
            val formattedTime = try {
                val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
                val dateObj = sdf24.parse(event.time)
                val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
                "${event.date} ${sdf12.format(dateObj!!)}"
            } catch (e: Exception) {
                "${event.date} ${event.time}"
            }
            Text(formattedTime, style = MaterialTheme.typography.body2)

            if (event.location.isNotBlank()) {
                Text(
                    text = event.location,
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.clickable {
                        val mapIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=${Uri.encode(event.location)}")
                        )
                        context.startActivity(mapIntent)
                    }
                )
            }

            Text("Reminder: ${if (event.reminder) "Yes" else "No"}", style = MaterialTheme.typography.body2)
            Text("Attendees: ${event.attendees.joinToString(", ")}", style = MaterialTheme.typography.body2)
            Spacer(Modifier.height(8.dp))

            if (deletingEventId == event.id) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                deleteError?.let { Text("Error: $it", color = MaterialTheme.colors.error) }
                Button(
                    onClick = {
                        onDeletingChange(event.id)
                        onDeleteError(null)
                        eventViewModel.deleteEvent(event)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete", color = MaterialTheme.colors.onPrimary)
                }
            }
        }
    }
}

/**
 * Displays list of events to the user.
 * Handles loading, error, and empty states.
 * Allows adding, editing, and deleting events.
 * Navigates using NavController.
 */
package com.example.eventplanner.userint

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventplanner.Screen
import com.example.eventplanner.data.EventEntity
import com.example.eventplanner.util.Resource
import com.example.eventplanner.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import com.example.eventplanner.NavGraph
import java.util.*

@Composable
fun HomeScreen(
    navController: NavController,
    eventViewModel: EventViewModel
) {
    val context = LocalContext.current
    val eventsResource by eventViewModel.events.collectAsState()

    var deletingEventId by remember { mutableStateOf<Int?>(null) }
    var deleteError by remember { mutableStateOf<String?>(null) }
    // button - add event
    Scaffold(
        topBar = { TopAppBar(title = { Text("Event Planner") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddEvent.route) }) {
                Text("+")
            }
        }
    ) { padding ->
        when (val resource = eventsResource) {
            // loading state
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) { CircularProgressIndicator() }
            } // error state
            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) { Text("Error: ${resource.message}", color = MaterialTheme.colors.error) }
            } // success state
            is Resource.Success -> {
                val events = resource.data
                // empty state
                if (events.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text("No events yet. Tap + to add one.", modifier = Modifier.padding(16.dp))
                    }
                } else {
                    // list of events
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        items(events) { event ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                elevation = 4.dp
                            ) { // event item container
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .clickable {
                                            navController.navigate(Screen.EditEvent.createRoute(event.id))
                                        }
                                ) { // text - event title & description
                                    Text(event.title, style = MaterialTheme.typography.h6)
                                    Text(event.description, style = MaterialTheme.typography.body2)
                                    Spacer(Modifier.height(4.dp))
                                    // text - formatted date & time
                                    val formattedTime = try {
                                        val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
                                        val dateObj = sdf24.parse(event.time)
                                        val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                        "${event.date} ${sdf12.format(dateObj!!)}"
                                    } catch (e: Exception) {
                                        "${event.date} ${event.time}"
                                    }
                                    Text(formattedTime, style = MaterialTheme.typography.body2)
                                    // text - location clickable
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
                                    // text - reminder
                                    Text(
                                        "Reminder: ${if (event.reminder) "Yes" else "No"}",
                                        style = MaterialTheme.typography.body2
                                    ) // text - attendees
                                    Text(
                                        "Attendees: ${event.attendees.joinToString(", ")}",
                                        style = MaterialTheme.typography.body2
                                    )

                                    Spacer(Modifier.height(8.dp))
                                    // loading indicator while deleting
                                    if (deletingEventId == event.id) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
                                        )
                                    } else { // text - delete error
                                        deleteError?.let {
                                            Text("Error: $it", color = MaterialTheme.colors.error)
                                        } // button - delete event
                                        Button(
                                            onClick = {
                                                deletingEventId = event.id
                                                deleteError = null
                                                eventViewModel.deleteEvent(event)
                                            },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                                            modifier = Modifier.fillMaxWidth()
                                        ) { Text("Delete", color = MaterialTheme.colors.onPrimary) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // reset delete state when events change
    LaunchedEffect(eventsResource) {
        if (eventsResource is Resource.Success || eventsResource is Resource.Error) {
            deletingEventId = null
            if (eventsResource is Resource.Error) deleteError = (eventsResource as Resource.Error).message
        }
    }
}
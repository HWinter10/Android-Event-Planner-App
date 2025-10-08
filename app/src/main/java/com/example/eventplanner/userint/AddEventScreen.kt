/**
 * Form for creating a new event.
 * Collects title, description, date, time, location, reminder, attendees.
 * Saves event via EventViewModel and optionally adds to Google Calendar.
 * Shows validation, loading indicators, and requests contacts permission.
 */
package com.example.eventplanner.userint

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.eventplanner.R
import com.example.eventplanner.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddEventScreen(
    eventViewModel: EventViewModel,
    onEventAdded: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    // Form state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var reminder by remember { mutableStateOf(false) }
    var attendees by remember { mutableStateOf(listOf<String>()) }

    // UI state
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Collect events state for feedback
    val eventsResourceState by eventViewModel.events.collectAsState()
    val eventsResource = eventsResourceState

    // Contact picker launcher
    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let {
            val cursor = activity.contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        val name = c.getString(nameIndex)
                        attendees = attendees + name
                    }
                }
            }
        }
    }

    // Permissions launcher
    val contactPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pickContactLauncher.launch(null)
        } else {
            Toast.makeText(context, "Contacts permission is required to add attendees", Toast.LENGTH_SHORT).show()
        }
    }

    // Reset form after successful addition
    LaunchedEffect(eventsResource) {
        if (isSaving) {
            when (eventsResource) {
                is com.example.eventplanner.util.Resource.Success -> {
                    title = ""
                    description = ""
                    location = ""
                    date = ""
                    time = ""
                    reminder = false
                    attendees = emptyList()
                    isSaving = false
                    onEventAdded()
                }
                is com.example.eventplanner.util.Resource.Error -> {
                    errorMessage = eventsResource.message
                    isSaving = false
                }
                else -> {}
            }
        }
    }

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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Add New Event", style = MaterialTheme.typography.h5)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Event Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        activity,
                        { _, y, m, d -> date = "%04d-%02d-%02d".format(y, m + 1, d) },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (date.isEmpty()) "Select Date" else date) }

            Button(
                onClick = {
                    val cal = Calendar.getInstance()
                    TimePickerDialog(
                        activity,
                        { _, h, m ->
                            val calSelected = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, h)
                                set(Calendar.MINUTE, m)
                            }
                            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                            time = sdf.format(calSelected.time)
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        false
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (time.isEmpty()) "Select Time" else time) }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Attendees: ${attendees.joinToString(", ")}")
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }) {
                    Text("Add Contact")
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Reminder")
                Spacer(Modifier.width(8.dp))
                Switch(checked = reminder, onCheckedChange = { reminder = it })
            }

            errorMessage?.let { Text("Error: $it", color = MaterialTheme.colors.error) }

            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            Button(
                onClick = {
                    if (title.isNotBlank() && date.isNotBlank() && time.isNotBlank()) {
                        errorMessage = null
                        isSaving = true

                        eventViewModel.addEvent(
                            title, description, date, time, location, reminder, attendees
                        )

                        // Google Calendar integration
                        try {
                            val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
                            val startMillis = sdf.parse("$date $time")?.time ?: System.currentTimeMillis()
                            val endMillis = startMillis + 60 * 60 * 1000
                            val calendarIntent = Intent(Intent.ACTION_INSERT).apply {
                                data = CalendarContract.Events.CONTENT_URI
                                putExtra(CalendarContract.Events.TITLE, title)
                                putExtra(CalendarContract.Events.DESCRIPTION, description)
                                putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                            }
                            activity.startActivity(calendarIntent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        errorMessage = "Title, date, and time are required"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) { Text("Save Event") }
        }
    }
}

/**
 * Form for editing an existing event.
 * Loads event by ID and allows updating details.
 * Handles validation, loading, and errors.
 * Saves changes via EventViewModel.
 */
package com.example.eventplanner.userint

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventplanner.data.EventEntity
import com.example.eventplanner.util.Resource
import com.example.eventplanner.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditEventScreen(
    navController: NavController,
    eventId: Int,
    eventViewModel: EventViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // collect events state
    val eventsResourceState by eventViewModel.events.collectAsState()
    val eventsResource = eventsResourceState

    // find event to edit
    val event = remember(eventsResource) {
        when (eventsResource) {
            is Resource.Success -> eventsResource.data.firstOrNull { it.id == eventId }
            else -> null
        }
    }
    // form state
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var location by remember { mutableStateOf(event?.location ?: "") }
    var date by remember { mutableStateOf(event?.date ?: "") }
    var time by remember { mutableStateOf(event?.time ?: "") }
    var reminder by remember { mutableStateOf(event?.reminder ?: false) }
    var isUpdating by remember { mutableStateOf(false) }
    var updateError by remember { mutableStateOf<String?>(null) }

    // show loading state
    if (eventsResource is Resource.Loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // show error if event not found
    if (event == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Event not found", style = MaterialTheme.typography.body1)
        }
        return
    }
    // form container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Edit Event", style = MaterialTheme.typography.h5)
        // inputs: title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Event Title") },
            modifier = Modifier.fillMaxWidth()
        )
        // inputs: description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        // inputs: location
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )
        // button - select date
        Button(onClick = {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                activity ?: return@Button,
                { _, y, m, d -> date = "%04d-%02d-%02d".format(y, m + 1, d) },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (date.isEmpty()) "Select Date" else date)
        }
        // button - select time
        Button(onClick = {
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            TimePickerDialog(
                activity ?: return@Button,
                { _, h, m ->
                    val calSelected = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, h)
                        set(Calendar.MINUTE, m)
                    }
                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calSelected.time)
                },
                hour,
                minute,
                false
            ).show()
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (time.isEmpty()) "Select Time" else time)
        }
        // reminder toggle
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Reminder")
            Spacer(Modifier.width(8.dp))
            Switch(checked = reminder, onCheckedChange = { reminder = it })
        }
        // text - update error
        updateError?.let { Text("Error: $it", color = MaterialTheme.colors.error) }
        // loading indicator during update
        if (isUpdating) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        // button - save changes
        Button(
            onClick = {
                if (title.isNotBlank() && date.isNotBlank() && time.isNotBlank()) {
                    isUpdating = true
                    updateError = null
                    val updatedEvent = event.copy(
                        title = title,
                        description = description,
                        location = location,
                        date = date,
                        time = time,
                        reminder = reminder
                    )
                    eventViewModel.updateEvent(updatedEvent)
                } else {
                    updateError = "Title, date, and time are required"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
    // handle update results
    LaunchedEffect(eventsResource) {
        if (isUpdating) {
            when (eventsResource) {
                is Resource.Success -> navController.popBackStack()
                is Resource.Error -> {
                    isUpdating = false
                    updateError = eventsResource.message
                }
                else -> {}
            }
        }
    }
}
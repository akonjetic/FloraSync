package com.example.florasync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.florasync.viewmodel.TaskViewModel
import com.example.florasync.viewmodel.LocalPlantsViewModel
import com.example.florasync.database.entities.MyPlant
import com.example.florasync.database.entities.TaskOccurrenceWithTask
import com.example.florasync.ui.components.AddTaskDialog
@Composable
fun TaskTabContent(
    plantId: Long,
    taskViewModel: TaskViewModel,
    allPlants: List<MyPlant>
) {
    val tasks by taskViewModel.getTasksForPlant(plantId).collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { showDialog = true }) {
                Text("Add Task")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (tasks.isEmpty()) {
            Text("No tasks found.", color = Color.Gray)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp)
            ) {
                items(tasks.size) { i ->
                    val task = tasks[i]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(task.icon + " " + task.title, fontWeight = FontWeight.Medium)
                                Text(
                                    "Repeats: ${task.repeatDays.joinToString()}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

        }

        if (showDialog) {
            AddTaskDialog(
                plantId = plantId,
                allPlants = allPlants,
                onAdd = {
                    taskViewModel.addTask(it)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}


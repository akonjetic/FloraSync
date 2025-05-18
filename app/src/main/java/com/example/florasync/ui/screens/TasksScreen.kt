package com.example.florasync.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import com.example.florasync.ui.components.TaskCard

@Composable
fun TasksScreen(
    taskViewModel: TaskViewModel = viewModel(),
    localPlantsViewModel: LocalPlantsViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf("today") }
    var showDialog by remember { mutableStateOf(false) }

    val todayTasks by taskViewModel.todayTasks.collectAsState()
    val upcomingTasks by taskViewModel.upcomingTasks.collectAsState()
    val completedTasks by taskViewModel.completedTasks.collectAsState()
    val allPlants by localPlantsViewModel.myPlants.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        taskViewModel.loadAllTasks()
    }

    val tabs = listOf("today", "upcoming", "completed")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Plant Care Tasks", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { showDialog = true }) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Add Task")
            }
        }

        ScrollableTabRow(
            selectedTabIndex = tabs.indexOf(selectedTab),
            modifier = Modifier.padding(vertical = 12.dp),
            edgePadding = 0.dp
        ) {
            tabs.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        val currentList = when (selectedTab) {
            "today" -> todayTasks
            "upcoming" -> upcomingTasks
            "completed" -> completedTasks
            else -> emptyList()
        }

        if (currentList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tasks $selectedTab", color = Color.Gray)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                items(currentList) { task ->
                    TaskCard(
                        task = task,
                        isCompleted = selectedTab == "completed",
                        onMarkDone = {
                            taskViewModel.markTaskCompleted(task.id, true)
                        },
                        onDelete = {
                            taskViewModel.deleteTaskById(task.taskId)
                        }
                    )
                }
            }
        }

        if (showDialog) {
            AddTaskDialog(
                plantId = null, // omogućuje izbor biljke
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




package com.example.florasync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.florasync.viewmodel.TaskViewModel
import com.example.florasync.viewmodel.LocalPlantsViewModel
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

    val tabs = listOf("Today", "Upcoming", "Completed")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 16.dp)
    ) {
        // Naslov i gumb za dodavanje
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Plant Care Tasks",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Button(onClick = { showDialog = true }) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Add Task")
            }
        }

        // Segmentirani tabovi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .background(Color(0xFFF1F1F1), RoundedCornerShape(16.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab.lowercase()
                val backgroundColor = if (isSelected) Color.White else Color.Transparent
                val textColor = if (isSelected) Color(0xFF2E7D32) else Color.DarkGray

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .clickable { selectedTab = tab.lowercase() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Prikaz zadataka za trenutni tab
        val currentList = when (selectedTab) {
            "today" -> todayTasks
            "upcoming" -> upcomingTasks
            "completed" -> completedTasks
            else -> emptyList()
        }

        if (currentList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tasks ${selectedTab}", color = Color.Gray)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

        // Dijalog za dodavanje zadatka
        if (showDialog) {
            AddTaskDialog(
                plantId = null,
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

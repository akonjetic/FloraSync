package com.example.florasync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.florasync.database.entities.DiaryEntry
import com.example.florasync.ui.components.DiaryEntryCard
import com.example.florasync.ui.components.DiaryEntryDetailDialog
import com.example.florasync.ui.components.GradientButton
import com.example.florasync.ui.components.HomeScreenHeaderSection
import com.example.florasync.ui.components.PlaceholderCard
import com.example.florasync.ui.components.PlantCardPlaceholder
import com.example.florasync.ui.components.SectionTitle
import com.example.florasync.ui.components.TaskCard
import com.example.florasync.viewmodel.DiaryViewModel
import com.example.florasync.viewmodel.TaskViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavController,
    taskViewModel: TaskViewModel,
    diaryViewModel: DiaryViewModel
) {
    var loading by remember { mutableStateOf(true) }
    var selectedDiaryEntry by remember { mutableStateOf<DiaryEntry?>(null) }

    val todayTasks by taskViewModel.todayTasks.collectAsState()
    val allDiaryEntries by diaryViewModel.allEntries.collectAsState(emptyList())

    LaunchedEffect(Unit) {
        delay(1000)
        loading = false
        taskViewModel.loadAllTasks()
    }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HomeScreenHeaderSection()

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SectionTitle("Today's Tasks")

                if (loading) {
                    repeat(2) {
                        PlantCardPlaceholder()
                    }
                } else {
                    if (todayTasks.isEmpty()) {
                        Text("No tasks for today. All clear! 🌿", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        todayTasks.take(2).forEach { task ->
                            TaskCard(
                                task = task,
                                isCompleted = false,
                                onMarkDone = {},
                                onDelete = {}
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    GradientButton(text = "View All Plants") {
                        navController.navigate("plants")
                    }
                }
            }
        }


        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SectionTitle("Recent Diary Entries")

                if (loading) {
                    repeat(2) {
                        PlaceholderCard()
                    }
                } else {
                    val recentEntries = allDiaryEntries.take(2)
                    if (recentEntries.isEmpty()) {
                        Text("No diary entries yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        recentEntries.forEach { entry ->
                            DiaryEntryCard(entry = entry, onClick = { selectedDiaryEntry = entry })
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    GradientButton(text = "View All Diary Entries") {
                        navController.navigate("diary")
                    }
                }
            }
        }
    }

    selectedDiaryEntry?.let {
        DiaryEntryDetailDialog(entry = it, onDismiss = { selectedDiaryEntry = null })
    }
}








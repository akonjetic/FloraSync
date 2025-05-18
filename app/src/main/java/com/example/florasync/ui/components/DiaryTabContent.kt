package com.example.florasync.ui.components

import androidx.compose.runtime.Composable
import com.example.florasync.viewmodel.DiaryViewModel

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
import com.example.florasync.database.entities.DiaryEntry
import com.example.florasync.viewmodel.TaskViewModel
import com.example.florasync.viewmodel.LocalPlantsViewModel
import com.example.florasync.database.entities.MyPlant
import com.example.florasync.database.entities.TaskOccurrenceWithTask
import com.example.florasync.ui.components.AddTaskDialog

@Composable
fun DiaryTabContent(
    plantId: Long,
    diaryViewModel: DiaryViewModel
) {
    val entries by diaryViewModel.getEntriesForPlant(plantId).collectAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<DiaryEntry?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { showDialog = true }) { Text("Add Entry") }
        }

        LazyColumn(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)) {
            items(entries) { entry ->
                DiaryEntryCard(entry, onClick = { selectedEntry = entry })
            }
        }

        selectedEntry?.let {
            DiaryEntryDetailDialog(entry = it, onDismiss = { selectedEntry = null })
        }

        if (showDialog) {
            AddDiaryEntryDialog(
                plantId = plantId,
                onAdd = {
                    diaryViewModel.addEntry(it)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }

    }
}
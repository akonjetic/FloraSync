package com.example.florasync.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.florasync.database.entities.DiaryEntry
import com.example.florasync.ui.components.AddDiaryEntryDialog
import com.example.florasync.ui.components.DiaryEntryCard
import com.example.florasync.ui.components.DiaryEntryDetailDialog
import com.example.florasync.viewmodel.DiaryViewModel

@Composable
fun DiaryScreen(viewModel: DiaryViewModel = viewModel()) {
    val allEntries by viewModel.allEntries.collectAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<DiaryEntry?>(null) }


    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("🌱 Plant Diary", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Button(onClick = { showDialog = true }) {
                Text("Add Entry")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (allEntries.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No diary entries yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                items(allEntries) { entry ->
                    DiaryEntryCard(entry = entry, onClick = { selectedEntry = entry }
                    )
                }
            }
        }

        selectedEntry?.let {
            DiaryEntryDetailDialog(entry = it, onDismiss = { selectedEntry = null })
        }


        if (showDialog) {
            AddDiaryEntryDialog(
                plantId = null,
                onAdd = {
                    viewModel.addEntry(it)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}


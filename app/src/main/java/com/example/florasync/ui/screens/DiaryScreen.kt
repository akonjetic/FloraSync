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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.florasync.R
import com.example.florasync.database.entities.DiaryEntry
import com.example.florasync.ui.components.AddDiaryEntryDialog
import com.example.florasync.ui.components.DiaryEntryDetailDialog
import com.example.florasync.viewmodel.DiaryViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DiaryScreen(viewModel: DiaryViewModel = viewModel()) {
    val allEntries by viewModel.allEntries.collectAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<DiaryEntry?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 16.dp)
    ) {
        // Naslov i gumb
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Plant Diary",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Button(onClick = { showDialog = true }) {
                Text("Add Entry")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (allEntries.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No diary entries yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 100.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(allEntries) { entry ->
                    DiaryCard(entry = entry, onClick = { selectedEntry = entry })
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

@Composable
fun DiaryCard(entry: DiaryEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = entry.activity!!,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDate(entry.date),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = entry.description!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    maxLines = 3
                )
            }

            // Slika s desne strane
            AsyncImage(
                model = "http://192.168.1.37:5072${entry.imageUri}",
                contentDescription = "Diary Image",
                placeholder = painterResource(id = R.drawable.placeholder), // zamijeni sa stvarnim
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            )
        }
    }
}


fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
    return date.format(formatter)
}

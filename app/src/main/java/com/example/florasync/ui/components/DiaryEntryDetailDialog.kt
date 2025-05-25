package com.example.florasync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.florasync.database.entities.DiaryEntry
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DiaryEntryDetailDialog(entry: DiaryEntry, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Text(
                text = entry.activity ?: "Diary Entry",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Slika
                if (!entry.imageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = "http://192.168.1.37:5072${entry.imageUri}",
                        contentDescription = "Diary Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                    )
                }

                // Datum s ikonom
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatDate(entry.date),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                // Podaci o biljkama
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    entry.height?.let {
                        LabeledData(label = "Height", value = "${it} cm", emoji = "📏")
                    }
                    entry.leafSize?.let {
                        LabeledData(label = "Leaf Size", value = "${it} cm", emoji = "🍃")
                    }
                    entry.flowerCount?.let {
                        LabeledData(label = "Flowers", value = "$it", emoji = "🌸")
                    }
                    entry.description?.let {
                        LabeledData(label = "Notes", value = it, emoji = "📝")
                    }
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    )
}

@Composable
fun LabeledData(label: String, value: String, emoji: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(text = emoji, modifier = Modifier.padding(end = 6.dp))
        Column {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Formatiranje LocalDate
fun formatDate(date: java.time.LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
    return date.format(formatter)
}

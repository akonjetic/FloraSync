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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.florasync.database.entities.DiaryEntry

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
                    Text(entry.activity ?: "Diary Entry", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (!entry.imageUri.isNullOrBlank()) {
                            AsyncImage(
                                model = "http://192.168.1.37:5072${entry.imageUri}",
                                contentDescription = "Diary Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }

                        Text("📅 ${entry.date}", fontWeight = FontWeight.SemiBold)
                        entry.activity?.let { Text("🌿 Activity: $it") }
                        entry.height?.let { Text("📏 Height: ${it}cm") }
                        entry.leafSize?.let { Text("🍃 Leaf Size: ${it}cm") }
                        entry.flowerCount?.let { Text("🌸 Flowers: $it") }
                        entry.description?.let { Text("📝 $it") }
                    }
                }
            )
        }
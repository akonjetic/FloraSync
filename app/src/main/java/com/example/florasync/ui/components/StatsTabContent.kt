package com.example.florasync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.florasync.viewmodel.DiaryViewModel

@Composable
fun StatsTabContent(
    plantId: Long,
    diaryViewModel: DiaryViewModel
) {
    val entries by diaryViewModel.getEntriesForPlant(plantId).collectAsState(emptyList())

    if (entries.isEmpty()) {
        Text("No data available for statistics.", color = Color.Gray)
        return
    }

    val sortedEntries = entries.sortedBy { it.date }
    val latest = sortedEntries.last()
    val previous = sortedEntries.getOrNull(sortedEntries.size - 2)

    fun diff(curr: Float?, prev: Float?) = if (curr != null && prev != null) (curr - prev).toInt() else null

    val heightDiff = diff(latest.height, previous?.height)
    val leafDiff = diff(latest.leafSize, previous?.leafSize)
    val flowerDiff = latest.flowerCount?.minus(previous?.flowerCount ?: 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title centered
        Text(
            "Growth Statistics",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Statistic cards in a Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            GrowthStatCard("Height", latest.height, "cm", heightDiff, Color(0xFF4CAF50), Modifier.weight(1f))
            GrowthStatCard("Leaf Size", latest.leafSize, "cm", leafDiff, Color(0xFF2196F3), Modifier.weight(1f))
            GrowthStatCard("Flowers", latest.flowerCount?.toFloat(), "", flowerDiff, Color(0xFFE91E63), Modifier.weight(1f))
        }

        // Measurement table
        Text(
            "Measurement History",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Table headers
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(12.dp)) {
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TableHeaderCell("Date", Modifier.weight(1f))
                    TableHeaderCell("Height", Modifier.weight(1f))
                    TableHeaderCell("Leaf Size", Modifier.weight(1f))
                    TableHeaderCell("Flowers", Modifier.weight(1f))
                }

                Divider(color = Color.LightGray)

                sortedEntries.forEach { entry ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TableCell(entry.date.toString(), Modifier.weight(1f))
                        TableCell(entry.height?.toString() ?: "-", Modifier.weight(1f))
                        TableCell(entry.leafSize?.toString() ?: "-", Modifier.weight(1f))
                        TableCell(entry.flowerCount?.toString() ?: "-", Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        modifier = modifier,
        color = Color.DarkGray
    )
}

@Composable
fun TableCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier,
        color = Color.Black
    )
}

@Composable
fun GrowthStatCard(
    label: String,
    currentValue: Float?,
    unit: String,
    diff: Int?,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(
                text = currentValue?.let { "${it.toInt()} $unit" } ?: "-",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = color
            )
            diff?.let {
                val arrow = if (it >= 0) "↑" else "↓"
                val diffColor = if (it >= 0) Color(0xFF4CAF50) else Color.Red
                Text(
                    "$arrow ${kotlin.math.abs(it)} $unit",
                    fontSize = 12.sp,
                    color = diffColor
                )
            }
        }
    }
}


// AddTaskDialog.kt
package com.example.florasync.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.florasync.database.entities.MyPlant
import com.example.florasync.database.entities.PlantTask
import java.time.LocalDate

@Composable
fun AddTaskDialog(
    plantId: Long?,
    allPlants: List<MyPlant>,
    onAdd: (PlantTask) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    val selectedDays = remember { mutableStateListOf<String>() }
    var selectedIcon by remember { mutableStateOf("💧") }
    var selectedPlantId by remember { mutableStateOf(plantId ?: allPlants.firstOrNull()?.id ?: 0L) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Choose Plant", style = MaterialTheme.typography.labelMedium)
                DropdownMenuBox(selectedId = selectedPlantId, items = allPlants) {
                    selectedPlantId = it
                }

                Text("Repeat On Days", style = MaterialTheme.typography.labelMedium)
                val allDays = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")

                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    allDays.forEach { day ->
                        AssistChip(
                            onClick = {
                                if (selectedDays.contains(day)) selectedDays.remove(day)
                                else selectedDays.add(day)
                            },
                            label = { Text(day.take(3)) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (day in selectedDays)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }

                Text("Choose Icon", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("💧", "☀️", "🍃").forEach { icon ->
                        AssistChip(
                            onClick = { selectedIcon = icon },
                            label = { Text(icon) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedIcon == icon)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && selectedDays.isNotEmpty()) {
                        onAdd(
                            PlantTask(
                                plantId = selectedPlantId,
                                title = title.trim(),
                                icon = selectedIcon,
                                repeatDays = selectedDays.toList(),
                                createdAt = LocalDate.now()
                            )
                        )
                        onDismiss()
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DropdownMenuBox(
    selectedId: Long,
    items: List<MyPlant>,
    onSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = items.find { it.id == selectedId }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected?.nickname.takeIf { !it.isNullOrBlank() } ?: selected?.name ?: "Select plant")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { plant ->
                DropdownMenuItem(
                    text = { Text(plant.nickname.takeIf { it?.isNotBlank()!! } ?: plant.name) },
                    onClick = {
                        onSelected(plant.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

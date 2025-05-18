package com.example.florasync.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.florasync.database.entities.TaskOccurrenceWithTask

@Composable
fun TaskCard(
    task: TaskOccurrenceWithTask,
    isCompleted: Boolean,
    onMarkDone: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth().padding(vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).background(Color(0xFFEFF1F5), shape = RoundedCornerShape(50)).wrapContentSize(
                        Alignment.Center)
                ) {
                    Text(task.icon, fontSize = 16.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(task.title, fontWeight = FontWeight.Medium)
                    Text("Date: ${task.date}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("Plant: ${task.nickname ?: "Unknown"}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }

            }

            if (!isCompleted) {
                OutlinedButton(onClick = onMarkDone) {
                    Text("Done", fontSize = 13.sp)
                }
            } else {
                OutlinedButton(onClick = onDelete, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) {
                    Text("Delete", fontSize = 13.sp)
                }
            }
        }
    }
}
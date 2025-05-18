package com.example.florasync.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun HomeScreenHeaderSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            "FloraSync",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold
            )
        )
        Text("Let's care for your plants today!", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
    }
}
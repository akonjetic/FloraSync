package com.example.florasync.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlantCardPlaceholder() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.size(64.dp).background(Color.Gray))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Box(modifier = Modifier.height(16.dp).width(100.dp).background(Color.LightGray))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.height(8.dp).width(150.dp).background(Color.LightGray))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.height(6.dp).width(200.dp).background(Color.LightGray))
            }
        }
    }
}

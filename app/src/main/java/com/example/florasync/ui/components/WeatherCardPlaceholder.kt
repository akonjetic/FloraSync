package com.example.florasync.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WeatherCardPlaceholder() {
    Column(modifier = Modifier.padding(16.dp)) {
        Box(modifier = Modifier.height(24.dp).width(140.dp).background(Color.LightGray))
        Spacer(modifier = Modifier.height(12.dp))
        repeat(3) {
            Box(modifier = Modifier.height(16.dp).fillMaxWidth().background(Color.LightGray))
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

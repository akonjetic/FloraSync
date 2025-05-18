package com.example.florasync.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.florasync.model.Weather

@Composable
fun WeatherCard(todayWeather: Weather, onDetailsClick: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Indoor Conditions", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Icon(Icons.Default.Face, contentDescription = null)
                Text("${todayWeather.temperature}°C")
            }
            Column {
                Icon(Icons.Default.Face, contentDescription = null)
                Text("${todayWeather.humidity}%")
            }
            Column {
                Icon(Icons.Default.Face, contentDescription = null)
                Text(todayWeather.description)
            }
            Column {
                Icon(Icons.Default.Face, contentDescription = null)
                Text("${todayWeather.light} light")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onDetailsClick, modifier = Modifier.fillMaxWidth()) {
            Text("View Details")
        }
    }
}

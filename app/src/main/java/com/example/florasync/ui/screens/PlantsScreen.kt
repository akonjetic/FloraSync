package com.example.florasync.ui.screens

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.florasync.R
import com.example.florasync.model.Plant
import com.example.florasync.model.mappers.toDomainModel
import com.example.florasync.viewmodel.LocalPlantsViewModel

@Composable
fun PlantsScreen(navController: NavController, viewModel: LocalPlantsViewModel = viewModel()) {

    var searchQuery by remember { mutableStateOf("") }

    val myPlants by viewModel.myPlants.observeAsState(emptyList())

    val plants = remember(myPlants) { myPlants.map { it.toDomainModel() } }

    val filteredPlants = plants.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.typeDescription.contains(searchQuery, ignoreCase = true) || it.nickname?.contains(searchQuery, ignoreCase = true) == true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 16.dp)
    ) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "My Plants",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            IconButton(
                onClick = {
                    navController.navigate("all-plants")
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x1A2E7D32))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Plant", tint = Color(0xFF2E7D32))
            }

        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search plants...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(12.dp)
        )

        LazyColumn(contentPadding = PaddingValues(bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (filteredPlants.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No plants found", color = Color.Gray)
                    }
                }
            } else {
                items(filteredPlants.size) { index ->
                    val plant = filteredPlants[index]
                    PlantCard(plant = plant, onClick = {
                        navController.navigate("plant-detail/${plant.id}")
                    })
                }
            }
        }
    }
}

@Composable
fun PlantCard(plant: Plant, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            AsyncImage(
                model = "$BASE_URL${plant.imageUrl}",
                contentDescription = plant.name,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder),
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
                    .background(Color.LightGray)
            )

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = plant.nickname?.takeIf { it.isNotBlank() } ?: plant.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (!plant.nickname.isNullOrBlank()) {
                            Text(
                                text = plant.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }

                    Text(
                        text = plant.typeValue,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.DarkGray,
                        modifier = Modifier
                            .background(Color(0xFFF1F1F1), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text("💧 ${plant.waterDescription}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text("🌤️ ${plant.lightDescription}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text("🌡️ ${plant.temperatureDescription}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

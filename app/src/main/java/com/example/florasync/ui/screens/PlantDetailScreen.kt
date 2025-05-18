package com.example.florasync.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.florasync.database.entities.MyPlant
import com.example.florasync.model.Plant
import com.example.florasync.ui.components.DiaryTabContent
import com.example.florasync.ui.components.StatsTabContent
import com.example.florasync.ui.components.TaskTabContent
import com.example.florasync.viewmodel.DiaryViewModel
import com.example.florasync.viewmodel.LocalPlantsViewModel
import com.example.florasync.viewmodel.TaskViewModel

@Composable
fun PlantDetailScreen(
    plant: Plant,
    navController: NavController,
    viewModel: LocalPlantsViewModel,
    taskViewModel: TaskViewModel,
    diaryViewModel: DiaryViewModel,
    allPlants: List<MyPlant>
) {
    var selectedTab by remember { mutableStateOf("Care") }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.TopStart
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("http://192.168.1.37:5072/${plant.imageUrl}")
                    .crossfade(true)
                    .build(),
                contentDescription = plant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(50))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    if (!plant.nickname.isNullOrBlank()) {
                        Text(plant.nickname, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            plant.name,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    } else {
                        Text(plant.name, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                }

                Text(
                    plant.typeDescription,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color(0xFFE0F2F1), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = plant.funFact,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(selectedTabIndex = listOf("Care", "Tasks", "Diary", "Stats").indexOf(selectedTab)) {
                listOf("Care", "Tasks", "Diary", "Stats").forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == "Care") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CareAttribute(icon = plant.waterIcon, label = plant.waterValue)
                    CareAttribute(icon = plant.lightIcon, label = plant.lightValue)
                    CareAttribute(icon = plant.temperatureIcon, label = plant.temperatureValue)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Care Instructions", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                CareItem(plant.waterIcon, "Watering", plant.waterDescription)
                CareItem(plant.lightIcon, "Light", plant.lightDescription)
                CareItem(plant.temperatureIcon, "Temperature", plant.temperatureDescription)

                OutlinedButton(
                    onClick = {
                        viewModel.removePlantById(plant.id)
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Remove", color = Color.Red)
                }
            } else if (selectedTab == "Tasks"){
                TaskTabContent(plantId = plant.id, taskViewModel = taskViewModel, allPlants = allPlants)
            } else if (selectedTab == "Diary") {
                DiaryTabContent(
                    plantId = plant.id,
                    diaryViewModel = diaryViewModel
                )
            } else if (selectedTab == "Stats") {
                StatsTabContent(plantId = plant.id, diaryViewModel = diaryViewModel)
            }

            else {
                Text(
                    text = "$selectedTab view coming soon...",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))



        }
    }
}

@Composable
fun CareAttribute(icon: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp)
    }
}

@Composable
fun CareItem(icon: String, title: String, description: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 16.sp, modifier = Modifier.padding(end = 6.dp))
            Text(title, fontWeight = FontWeight.Medium)
        }
        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 24.dp)
        )
    }
}

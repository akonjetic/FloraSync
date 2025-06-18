package com.example.florasync.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.florasync.viewmodel.NetworkViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.florasync.R
import com.example.florasync.model.dto.PlantDto
import com.example.florasync.viewmodel.LocalPlantsViewModel

const val BASE_URL = "http://192.168.1.37:5072/"

@Composable
fun AllAvailablePlantsScreen(
    viewModel: NetworkViewModel = viewModel(),
    localViewModel: LocalPlantsViewModel = viewModel()
) {
    val plants = viewModel.plants.collectAsLazyPagingItems()
    val searchText by viewModel.searchQuery.collectAsState()

    var selectedPlant by remember { mutableStateOf<PlantDto?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var nickname by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 16.dp)
    ) {

        Text(
            "All Available Plants",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Search plants...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(plants.itemCount) { index ->
                val plant = plants[index]
                if (plant != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPlant = plant
                                showDialog = true
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("${BASE_URL}${plant.imageUrl}")
                                    .crossfade(true)
                                    .build(),
                                contentDescription = plant.name,
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.placeholder),
                                error = painterResource(id = R.drawable.placeholder),
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = plant.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            when (plants.loadState.append) {
                is LoadState.Loading -> item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Error -> item {
                    Text("Error loading more plants.", color = Color.Red)
                }

                else -> {}
            }
        }
    }

    if (showDialog && selectedPlant != null) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                nickname = ""
            },
            confirmButton = {
                Button(
                    onClick = {
                        localViewModel.insertMyPlant(selectedPlant!!, nickname)
                        showDialog = false
                        nickname = ""
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Add", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        nickname = ""
                    }
                ) {
                    Text("Cancel")
                }
            },
            title = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Add to your collection",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        selectedPlant!!.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Enter a nickname (optional)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        placeholder = { Text("e.g. Kitchen Basil") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }


}




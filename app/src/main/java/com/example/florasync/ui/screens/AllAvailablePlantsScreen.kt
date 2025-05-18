package com.example.florasync.ui.screens

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

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            "All Available Plants",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
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
            shape = RoundedCornerShape(12.dp)
        )

        LazyColumn {
            val filtered = plants.itemSnapshotList.items.filter {
                it.name.contains(searchText, ignoreCase = true) ||
                        it.type.contains(searchText, ignoreCase = true)
            }

            items(filtered) { plant ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            selectedPlant = plant
                            showDialog = true
                        },
                    shape = RoundedCornerShape(12.dp)
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

            // Loading / Error Indicators
            plants.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { CircularProgressIndicator(modifier = Modifier.fillMaxWidth()) }
                    }
                    loadState.append is LoadState.Loading -> {
                        item { CircularProgressIndicator(modifier = Modifier.fillMaxWidth()) }
                    }
                    loadState.append is LoadState.Error -> {
                        item { Text("Greška kod učitavanja podataka") }
                    }
                }
            }
        }
    }

    // Alert Dialog
    var nickname by remember { mutableStateOf("") }

    if (showDialog && selectedPlant != null) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                nickname = ""
            },
            confirmButton = {
                TextButton(onClick = {
                    localViewModel.insertMyPlant(selectedPlant!!, nickname)
                    showDialog = false
                    nickname = ""
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    nickname = ""
                }) {
                    Text("Cancel")
                }
            },
            title = { Text("Add plant to your collection?") },
            text = {
                Column {
                    Text("Enter a nickname to identify this plant:")
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        singleLine = true,
                        placeholder = { Text("e.g. Kitchen Basil") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            }
        )
    }

}



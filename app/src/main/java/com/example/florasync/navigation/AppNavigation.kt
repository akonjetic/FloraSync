package com.example.florasync.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.florasync.ui.screens.*
import com.example.florasync.viewmodel.LocalPlantsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.florasync.model.mappers.toDomainModel
import com.example.florasync.navigation.Routes.PLANT_DETAIL
import com.example.florasync.viewmodel.DiaryViewModel
import com.example.florasync.viewmodel.TaskViewModel

object Routes {
    const val HOME = "home"
    const val PLANTS = "plants"
    const val TASKS = "tasks"
    const val DIARY = "diary"
    const val SCAN = "scan"
    const val ALL_PLANTS = "all-plants"
    const val PLANT_DETAIL = "plant-detail/{plantId}"
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            val taskViewModel: TaskViewModel = viewModel()
            val diaryViewModel: DiaryViewModel = viewModel()
            HomeScreen(
                navController,
                taskViewModel = taskViewModel,
                diaryViewModel = diaryViewModel
            )
        }
        composable(Routes.PLANTS) { PlantsScreen(navController) }
        composable(Routes.TASKS) { TasksScreen() }
        composable(Routes.DIARY) { DiaryScreen() }
        composable(Routes.SCAN) { ScanScreen() }
        composable(Routes.ALL_PLANTS) { AllAvailablePlantsScreen() }
        composable(
            route = PLANT_DETAIL,
            arguments = listOf(navArgument("plantId") { type = NavType.LongType })
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getLong("plantId") ?: return@composable
            val viewModel: LocalPlantsViewModel = viewModel()
            val taskViewModel: TaskViewModel = viewModel()
            val diaryViewModel: DiaryViewModel = viewModel()
            val myPlants by viewModel.myPlants.observeAsState(emptyList())
            val allPlants by viewModel.myPlants.observeAsState(emptyList())
            val selectedPlant = myPlants.find { it.id == plantId }?.toDomainModel()

            if (selectedPlant != null) {
                PlantDetailScreen(
                    plant = selectedPlant,
                    navController = navController,
                    viewModel = viewModel,
                    taskViewModel = taskViewModel,
                    diaryViewModel = diaryViewModel,
                    allPlants = allPlants
                )
            } else {
                Text("Plant not found", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

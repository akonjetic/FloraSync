package com.example.florasync.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.florasync.navigation.Routes
import com.example.florasync.R


data class NavItem(val label: String, val iconResId: Int, val route: String)

val bottomNavItems = listOf(
    NavItem("Home", R.drawable.plant_house, Routes.HOME),
    NavItem("Plants", R.drawable.my_plants, Routes.PLANTS),
    NavItem("Tasks", R.drawable.tasks_watering, Routes.TASKS),
    NavItem("Diary", R.drawable.diary_plant, Routes.DIARY),
    NavItem("Scan", R.drawable.scan_plant, Routes.SCAN)
)

@Composable
fun BottomBar(navController: NavController) {
    val navBackStack = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack.value?.destination?.route

    val highlightColor = Color(0xFF2E7D32)
    val selectedBackground = Color(0xFFE8F5E9)

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.label,
                        tint = highlightColor,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = highlightColor,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = highlightColor,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = selectedBackground
                )
            )
        }
    }
}

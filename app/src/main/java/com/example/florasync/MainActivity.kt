package com.example.florasync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.florasync.navigation.AppNavigation
import com.example.florasync.ui.theme.FloraSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FloraSyncTheme(darkTheme = false) {
                FloraSyncApp()
            }
        }
    }
}
package com.example.florasync.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.AsyncImagePainter


import com.example.florasync.database.entities.DiaryEntry

@Composable
fun DiaryImagePreview(imageUrl: String?) {
    if (imageUrl.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("No image", color = Color.DarkGray)
        }
    } else {
        val fullUrl = "http://192.168.1.37:5072$imageUrl"
        Log.d("DiaryImage", "Trying to load image from: $fullUrl")

        val painter = rememberAsyncImagePainter(fullUrl)
        val state = painter.state

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painter,
                contentDescription = "Diary image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            when (state) {
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator()
                }
                is AsyncImagePainter.State.Error -> {
                    Log.e("DiaryImage", "Image load failed: ${state.result.throwable}")
                    Text("⚠️ Failed to load image", color = Color.Red)
                }
                else -> {}
            }
        }
    }
}

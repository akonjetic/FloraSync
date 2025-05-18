package com.example.florasync.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScanScreen() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var result by remember { mutableStateOf<ScanResult?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedOrgans by remember { mutableStateOf("auto") }

    val context = LocalContext.current
    val cameraPermission = Manifest.permission.CAMERA
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, cameraPermission) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }


    var expanded by remember { mutableStateOf(false) }
    var currentPhotoPath by remember { mutableStateOf("") }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && imageUri != null) {
            isLoading = true
            sendToPlantNetApi(context, imageUri!!, selectedOrgans,
                onResult = {
                    result = it
                    showDialog = true
                    isLoading = false
                },
                onError = {
                    Toast.makeText(context, "Identification failed", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            )
        }
    }

    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {

        Text("🌿 Scan Plant", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Select organ type and image to identify", color = Color.Gray)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Organ:", fontWeight = FontWeight.Medium)
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(selectedOrgans)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("leaf", "flower", "fruit", "auto").forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                selectedOrgans = it
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                if (hasCameraPermission) {
                    val photoFile = createImageFile(context)
                    val uri = FileProvider.getUriForFile(context, "com.example.florasync.fileprovider", photoFile)
                    imageUri = uri
                    cameraLauncher.launch(uri)
                } else {
                    permissionLauncher.launch(cameraPermission)
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Star, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isLoading) "Identifying..." else "Take Photo")
        }

        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        if (showDialog && result != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Close")
                    }
                },
                title = {
                    Text(result!!.name, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Scientific: ${result!!.scientificName}", fontSize = 14.sp)
                        Text("Confidence: ${result!!.confidence}%", fontSize = 13.sp, color = Color.Gray)
                        Text(result!!.description, fontSize = 13.sp)
                    }
                }
            )
        }
    }
}

data class ScanResult(
    val name: String,
    val scientificName: String,
    val confidence: Int,
    val description: String
)

fun sendToPlantNetApi(
    context: Context,
    imageUri: Uri,
    organs: String,
    onResult: (ScanResult) -> Unit,
    onError: () -> Unit
) {
    val apiKey = "2b10rYDqsVdopnz3W8CwbHdpu"
    val inputStream = context.contentResolver.openInputStream(imageUri) ?: return onError()
    val imageBytes = inputStream.readBytes()
    val requestBody = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
    val multipartBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("organs", organs)
        .addFormDataPart("images", "image.jpg", requestBody)
        .build()

    val request = Request.Builder()
        .url("https://my-api.plantnet.org/v2/identify/all?api-key=$apiKey")
        .post(multipartBody)
        .build()

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("PlantNetAPI", "Network error: ${e.message}", e)
            Handler(Looper.getMainLooper()).post { onError() }
        }

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            Log.d("PlantNetAPI", "Response code: ${response.code}")
            Log.d("PlantNetAPI", "Response body: $body")

            if (response.isSuccessful && body != null) {
                try {
                    val json = JSONObject(body)
                    val results = json.getJSONArray("results")
                    if (results.length() == 0) {
                        Log.w("PlantNetAPI", "No results found.")
                        Handler(Looper.getMainLooper()).post { onError() }
                        return
                    }

                    val first = results.getJSONObject(0)
                    val species = first.getJSONObject("species")
                    val name = species.getJSONArray("commonNames").optString(0, "Unknown")
                    val scientific = species.getString("scientificNameWithoutAuthor")
                    val score = (first.getDouble("score") * 100).toInt()
                    val description = species.optString("family", "No description available.")

                    val scanResult = ScanResult(name, scientific, score, description)
                    Handler(Looper.getMainLooper()).post {
                        onResult(scanResult)
                    }

                } catch (e: Exception) {
                    Log.e("PlantNetAPI", "JSON parsing error: ${e.message}", e)
                    Handler(Looper.getMainLooper()).post { onError() }
                }
            } else {
                Log.e("PlantNetAPI", "Response failed: code=${response.code}, body=$body")
                Handler(Looper.getMainLooper()).post { onError() }
            }
        }
    })
}

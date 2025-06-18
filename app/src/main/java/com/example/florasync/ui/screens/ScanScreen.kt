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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.florasync.R
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
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    var expanded by remember { mutableStateOf(false) }
    var currentPhotoPath by remember { mutableStateOf("") }
    var dropdownWidth by remember { mutableStateOf(0) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && imageUri != null && success) {
            Log.d("ScanScreen", "Picture taken: $imageUri")
            isLoading = true
            sendToPlantNetApi(
                context, imageUri!!, selectedOrgans,
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
        } else {
            Log.w("ScanScreen", "Picture NOT taken or URI is null")
        }
    }

    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 48.dp)
    ) {
        // Naslov
        Text("Plant Scanner", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
        Text("Take a photo to identify your plant", color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords -> dropdownWidth = coords.size.width }
                    .clip(RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Organ: ${selectedOrgans.replaceFirstChar { it.uppercase() }}")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { dropdownWidth.toDp() })
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                listOf("leaf", "flower", "fruit", "auto").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            selectedOrgans = option
                            expanded = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Slika / placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF1F1F1)),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.placeholder),
                        contentDescription = "Camera Placeholder",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Point your camera at a plant to identify it", color = Color.DarkGray)
                }
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Captured Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x66000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gumb s paddingom
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
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .navigationBarsPadding(), // <- rješava overlap s BottomNav
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Identifying..." else "Start Scan")
        }

        Spacer(modifier = Modifier.height(84.dp))
    }

    // Rezultat dijalog
    if (showDialog && result != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            },
            shape = RoundedCornerShape(16.dp),
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = result!!.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = result!!.scientificName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Confidence score
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌿 Confidence:", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${result!!.confidence}%", color = Color.DarkGray)
                    }

                    // Plant family / description
                    Row(verticalAlignment = Alignment.Top) {
                        Text("🏷️ Family:", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(result!!.familyName, color = Color.DarkGray)
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        )
    }

}

data class ScanResult(
    val name: String,
    val scientificName: String,
    val confidence: Int,
    val familyName: String
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
                        Handler(Looper.getMainLooper()).post { onError() }
                        return
                    }

                    val first = results.getJSONObject(0)
                    val species = first.getJSONObject("species")
                    val name = species.getJSONArray("commonNames").optString(0, "Unknown")
                    val scientific = species.getString("scientificNameWithoutAuthor")
                    val score = (first.getDouble("score") * 100).toInt()
                    val familyObj = species.optJSONObject("family")
                    val familyName = familyObj?.optString("scientificName", "Unknown Family") ?: "Unknown Family"

                    val scanResult = ScanResult(name, scientific, score, familyName)
                    Handler(Looper.getMainLooper()).post { onResult(scanResult) }

                } catch (e: Exception) {
                    Log.e("PlantNetAPI", "JSON parsing error: ${e.message}", e)
                    Handler(Looper.getMainLooper()).post { onError() }
                }
            } else {
                Handler(Looper.getMainLooper()).post { onError() }
            }
        }
    })
}


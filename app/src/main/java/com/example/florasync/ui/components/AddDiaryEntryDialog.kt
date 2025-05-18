package com.example.florasync.ui.components



// dodaj ispravne
import android.app.VoiceInteractor
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.florasync.database.entities.DiaryEntry
import com.example.florasync.ui.screens.BASE_URL
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.time.LocalDate


@Composable
fun AddDiaryEntryDialog(
    plantId: Long?, // može biti null ako je opći zapis
    onAdd: (DiaryEntry) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }
    var activity by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var leafSize by remember { mutableStateOf("") }
    var flowerCount by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (imageUri != null) {
                    isUploading = true
                    uploadImageToApi(
                        context = context,
                        uri = imageUri!!,
                        onSuccess = { imageUrl ->
                            val entry = DiaryEntry(
                                plantId = plantId,
                                description = description,
                                activity = activity,
                                date = LocalDate.now(),
                                height = height.toFloatOrNull(),
                                leafSize = leafSize.toFloatOrNull(),
                                flowerCount = flowerCount.toIntOrNull(),
                                imageUri = imageUrl
                            )
                            onAdd(entry)
                            isUploading = false
                            onDismiss()
                        },
                        onFailure = {
                            Toast.makeText(context, "Upload failed.", Toast.LENGTH_SHORT).show()
                            isUploading = false
                        }
                    )
                } else {
                    val entry = DiaryEntry(
                        plantId = plantId,
                        description = description,
                        activity = activity,
                        date = LocalDate.now(),
                        height = height.toFloatOrNull(),
                        leafSize = leafSize.toFloatOrNull(),
                        flowerCount = flowerCount.toIntOrNull(),
                        imageUri = null
                    )
                    onAdd(entry)
                    onDismiss()
                }
            }, enabled = !isUploading) {
                Text(if (isUploading) "Uploading..." else "Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("New Diary Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                OutlinedTextField(value = activity, onValueChange = { activity = it }, label = { Text("Activity") })
                OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = leafSize, onValueChange = { leafSize = it }, label = { Text("Leaf size (cm)") }, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = flowerCount, onValueChange = { flowerCount = it }, label = { Text("Number of Flowers") }, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number))

                Button(onClick = { imagePicker.launch("image/*") }) {
                    Text("Choose Image")
                }

                imageUri?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Image selected", fontSize = 12.sp, color = Color.Gray)
                    AsyncImage(
                        model = it,
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(top = 4.dp),
                        contentScale = ContentScale.Crop
                    )
                }

            }
        }
    )


}

fun uploadImageToApi(
    context: Context,
    uri: Uri,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    val contentResolver = context.contentResolver
    val stream = contentResolver.openInputStream(uri) ?: return onFailure()
    val bytes = stream.readBytes()
    val fileName = "diary_${System.currentTimeMillis()}.jpg"

    val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
    val multipartBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("File", fileName, requestBody) // 👈 ovo backend očekuje
        .build()

    val request = Request.Builder()
        .url("http://192.168.1.37:5072/api/diary-images/upload") // 👈 tvoj API endpoint
        .post(multipartBody)
        .build()

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("UPLOAD", "Upload failed: ${e.message}")
            Handler(Looper.getMainLooper()).post { onFailure() }
        }


        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            if (response.isSuccessful && body != null) {
                val imageUrl = Regex("\"imageUrl\"\\s*:\\s*\"([^\"]+)\"").find(body)?.groupValues?.get(1)
                Handler(Looper.getMainLooper()).post { onSuccess(imageUrl ?: "") }
            } else {
                Handler(Looper.getMainLooper()).post { onFailure() }
            }
        }
    })
}

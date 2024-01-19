package com.example.myapplication.ui.theme

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UploadActivity : AppCompatActivity() {

    private val FILE_SIZE_THRESHOLD = 100 * 1024 * 1024 // 100MB
    private val PART_SIZE_THRESHOLD = 10 * 1024 * 1024 // 10MB

    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { fileUri ->
            val filePath = getFileFromUri(fileUri)
            if (filePath != null) {
                uploadFile(filePath).onEach { result ->
                    // Handle upload progress or errors

                }.launchIn(lifecycleScope)
            }
        }
    }

//    private fun getFileFromUri(uri: Uri): String? {
//        // Implement logic to get the file path from the URI
//        // Return the file path or null if not found
//    }

    private fun uploadFile(filePath: String): Flow<Result<Unit>> = flow {
        val file = File(filePath)
        if (!file.exists()) {
            emit(Result.failure(IOException("File not found: $filePath")))
            return@flow
        }

        val fileSize = file.length()
        if (fileSize > FILE_SIZE_THRESHOLD) {
            splitAndUploadFile(file, PART_SIZE_THRESHOLD.toLong()).collect { result ->
                emit(result)
            }
        } else {
            emit(uploadSingleFile(file))
        }
    }

    private suspend fun splitAndUploadFile(file: File, partSize: Long): Flow<Result<Unit>> = flow {
        val totalParts = calculateTotalParts(file.length(), partSize)
        for (partNumber in 1..totalParts) {
            val partFile = createPartFile(file, partNumber)
            val result = uploadSingleFile(partFile)
            emit(result)
        }
    }

    private suspend fun uploadSingleFile(file: File): Result<Unit> = withContext(Dispatchers.IO) {
        // Implement the logic to upload the file here
        try {
            // Upload the file
            Result.success(Unit)
        } catch (e: Exception) {
            // Handle any upload failure and emit an error result
            Result.failure(IOException("Failed to upload file: ${file.name}", e))
        }
    }

    private fun calculateTotalParts(fileSize: Long, partSize: Long): Int {
        return (fileSize / partSize).toInt() + if (fileSize % partSize == 0L) 0 else 1
    }

    private fun createPartFile(file: File, partNumber: Int): File {
        val partFileName = "${file.nameWithoutExtension}_part$partNumber.${file.extension}"
        return File(file.parent, partFileName)
    }

    private fun launchMediaPicker() {
        pickMediaLauncher.launch("image/*") // Change the MIME type as per your requirement
    }


    // Helper method to get the File object from the URI
    private fun getFileFromUri(uri: Uri): String? {
        var file: File? = null
        if (uri.scheme == "content") {
            // For content URIs (e.g., from the media provider)
            val documentFile = DocumentFile.fromSingleUri(this, uri)
            val displayName = documentFile!!.name
            val mimeType = contentResolver.getType(uri)
            if (displayName != null && mimeType != null) {
                // Create a temporary file with the display name and the appropriate file extension
                val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                if (fileExtension != null) {
                    try {
                        file = File.createTempFile(displayName, ".$fileExtension", cacheDir)
                        val inputStream = contentResolver.openInputStream(uri)
                        val outputStream = FileOutputStream(file)
                        if (inputStream != null && outputStream != null) {
                            val buffer = ByteArray(1024)
                            var length: Int
                            while ((inputStream.read(buffer).also { length = it }) > 0) {
                                outputStream.write(buffer, 0, length)
                            }
                            inputStream.close()
                            outputStream.close()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (uri.scheme == "file") {
            // For file URIs
            val filePath = uri.path
            if (filePath != null) {
                file = File(filePath)
            }
        }
        return file?.path
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()
            && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            // Permission is granted, launch the media picker
            launchMediaPicker()
        } else {
            // Permission is not granted, handle it accordingly (e.g., show an error message)
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    lateinit var txtMessage: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtMessage = findViewById(R.id.txt_message)

        findViewById<Button>(R.id.selectFileButton).setOnClickListener {
            // Check if the necessary permission is granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_VIDEO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                    ),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                // Permission is granted, launch the media picker
                launchMediaPicker()
            }
        }
    }
}
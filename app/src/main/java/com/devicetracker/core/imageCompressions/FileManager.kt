package com.devicetracker.core.imageCompressions

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI

class FileManager(
    private val context: Context
) {
    suspend fun saveImage(
        contentUri: Uri,
        fileName: String
    ) {
        withContext(Dispatchers.IO) {
            context
                .contentResolver
                .openInputStream(contentUri)
                ?.use { inputStream ->
                    context
                        .openFileOutput(fileName, Context.MODE_PRIVATE)
                        .use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                }
        }
    }

    suspend fun saveImage(
        bytes: ByteArray,
        fileName: String
    ) {
        withContext(Dispatchers.IO) {
            context
                .openFileOutput(fileName, Context.MODE_PRIVATE)
                .use { outputStream ->
                    outputStream.write(bytes)
                }
        }
    }

     fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun saveByteArrayToFile(context: Context, byteArray: ByteArray?, fileName: String): Uri? {
        val externalCacheDir = context.externalCacheDir
        if (externalCacheDir != null) {
            val file = File(externalCacheDir, fileName)
            var uri: Uri? = null
            try {
                FileOutputStream(file).use { fos ->
                    fos.write(byteArray)
                    fos.flush()
                    // Optionally notify the user about the successful save
                    Toast.makeText(context, "File saved to external cache", Toast.LENGTH_SHORT).show()
                }
                uri = Uri.parse(file.path)
            } catch (e: IOException) {
                e.printStackTrace()
                // Optionally notify the user about the error
                Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
            }
            return uri
        }
       return null
    }
}
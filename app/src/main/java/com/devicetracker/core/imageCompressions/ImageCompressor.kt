package com.devicetracker.core.imageCompressions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class ImageCompressor(
    private val context: Context
) {
    suspend fun compressImage(
        contentUri: Uri,
        compressionThreshold: Long
    ): ByteArray? {
        return withContext(Dispatchers.IO) {
            val mimeType = context.contentResolver.getType(contentUri)
            val inputBytes = context
                .contentResolver
                .openInputStream(contentUri)
                ?.use { inputStream ->
                    inputStream.readBytes()
                } ?: return@withContext null

            ensureActive()

            withContext(Dispatchers.Default) {
                val bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)
                if (bitmap == null) {
                    Log.e("ImageCompressor", "Bitmap is null, cannot compress image.")
                    return@withContext null
                }
                ensureActive()

                val compressFormat = when(mimeType) {
                    "image/png" -> Bitmap.CompressFormat.PNG
                    "image/jpeg" -> Bitmap.CompressFormat.JPEG
                    "image/webp" -> if(Build.VERSION.SDK_INT >= 30) {
                        Bitmap.CompressFormat.WEBP_LOSSLESS
                    } else Bitmap.CompressFormat.WEBP
                    else -> Bitmap.CompressFormat.JPEG
                }

                var outputBytes: ByteArray
                var quality = 100

                do {
                    ByteArrayOutputStream().use { outputStream ->
                        bitmap.compress(compressFormat, quality, outputStream)
                        outputBytes = outputStream.toByteArray()
                        quality -= (quality * 0.1).roundToInt()
                    }
                } while(isActive &&
                    outputBytes.size > compressionThreshold &&
                    quality > 5 &&
                    compressFormat != Bitmap.CompressFormat.PNG
                )

                outputBytes
            }
        }
    }

    suspend fun compressBitmapImage(
        contentUri: Uri,
        compressionThreshold: Long
    ): Bitmap? {
        return withContext(Dispatchers.IO) {
            val mimeType = context.contentResolver.getType(contentUri)
            val inputBytes = context
                .contentResolver
                .openInputStream(contentUri)
                ?.use { inputStream ->
                    inputStream.readBytes()
                } ?: return@withContext null

            ensureActive()

            withContext(Dispatchers.Default) {
                val bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)

                if (bitmap == null) {
                    Log.e("ImageCompressor", "Bitmap is null, cannot compress image.")
                    return@withContext null
                }

                ensureActive()

                val compressFormat = when(mimeType) {
                    "image/png" -> Bitmap.CompressFormat.PNG
                    "image/jpeg" -> Bitmap.CompressFormat.JPEG
                    "image/webp" -> if(Build.VERSION.SDK_INT >= 30) {
                        Bitmap.CompressFormat.WEBP_LOSSLESS
                    } else Bitmap.CompressFormat.WEBP
                    else -> Bitmap.CompressFormat.JPEG
                }

                var outputBytes: ByteArray
                var quality = 90

                do {
                    ByteArrayOutputStream().use { outputStream ->
                        bitmap.compress(compressFormat, quality, outputStream)
                        outputBytes = outputStream.toByteArray()
                        quality -= (quality * 0.1).roundToInt()
                    }
                } while(isActive &&
                    outputBytes.size > compressionThreshold &&
                    quality > 5 &&
                    compressFormat != Bitmap.CompressFormat.PNG
                )

                BitmapFactory.decodeByteArray(outputBytes, 0, outputBytes.size)
            }
        }
    }

    fun compressImageFile(uri: Uri, context: Context): Uri {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputFile = File(context.externalCacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        Log.d("ImageCompression", "nkp outputFile ${outputFile.path}")
        val outputStream = FileOutputStream(outputFile)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Adjust quality as needed

        outputStream.close()
        inputStream?.close()

        return Uri.fromFile(outputFile)
    }
}
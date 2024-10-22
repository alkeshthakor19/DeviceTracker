package com.devicetracker.core

import android.content.Context
import android.util.Log
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import com.devicetracker.core.Constants.TAG
import java.net.URLDecoder

class Utils {
    companion object {
        fun print(e: Exception) = Log.e(TAG, e.stackTraceToString())
        fun debugLog(tag: String, message: String) = Log.d(tag, message)

        fun showMessage(
            context: Context,
            message: String?
        ) = makeText(context, message, LENGTH_LONG).show()

        fun extractFilePathFromUrl(fileDownloadUrl: String?): String? {
            return try {
                val decodedUrl = URLDecoder.decode(fileDownloadUrl, "UTF-8")
                val queryStringIndex = decodedUrl.indexOf("?")
                if (queryStringIndex != -1) {
                    val pathAndQuery = decodedUrl.substring(0, queryStringIndex)
                    val pathIndex = pathAndQuery.indexOf("/o/")
                    if (pathIndex != -1) {
                        return pathAndQuery.substring(pathIndex + 3)
                    }
                }
                null
            } catch (e: Exception) {
                Log.w("Utils", "Error extracting file path from URL: ${e.message}")
                null
            }
        }
    }
}
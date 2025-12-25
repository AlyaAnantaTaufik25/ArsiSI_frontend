package com.example.arsisi_frontend.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

object FileUploadHelper {
    
    /**
     * Convert Uri to MultipartBody.Part for file upload
     */
    fun uriToMultipartBodyPart(
        context: Context,
        uri: Uri,
        partName: String = "file"
    ): MultipartBody.Part? {
        return try {
            // Get file from URI
            val file = uriToFile(context, uri) ?: return null
            
            // Get correct MIME type based on file extension
            val mimeType = getMimeType(file.name)
            
            // Create RequestBody with correct MIME type
            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
            
            // Create MultipartBody.Part
            MultipartBody.Part.createFormData(partName, file.name, requestBody)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Get MIME type from file name
     */
    private fun getMimeType(fileName: String): String {
        return when (fileName.substringAfterLast('.', "").lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            else -> "application/octet-stream"
        }
    }
    
    /**
     * Convert Uri to File
     */
    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = getFileName(context, uri) ?: "temp_file"
            val file = File(context.cacheDir, fileName)
            
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Get file name from Uri
     */
    private fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        return fileName
    }
    
    /**
     * Create RequestBody from String
     */
    fun createPartFromString(value: String): okhttp3.RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }
    
    /**
     * Get file size from Uri
     */
    fun getFileSize(context: Context, uri: Uri): String {
        var size = 0L
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    size = cursor.getLong(sizeIndex)
                }
            }
        }
        return formatFileSize(size)
    }
    
    /**
     * Format file size to human readable
     */
    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${(bytes / 1024.0).format(1)} KB"
            else -> "${(bytes / (1024.0 * 1024.0)).format(1)} MB"
        }
    }
    
    private fun Double.format(decimals: Int): String {
        return "%.${decimals}f".format(this)
    }
}

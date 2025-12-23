package com.example.arsisi_frontend.data.local.converter

import androidx.room.TypeConverter
import com.example.arsisi_frontend.data.local.entity.AttachmentData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AttachmentListConverter {
    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromAttachmentList(attachments: List<AttachmentData>?): String {
        if (attachments == null) return "[]"
        return gson.toJson(attachments)
    }

    @TypeConverter
    @JvmStatic
    fun toAttachmentList(data: String?): List<AttachmentData> {
        if (data.isNullOrEmpty()) return emptyList()
        return try {
            val listType = object : TypeToken<List<AttachmentData>>() {}.type
            gson.fromJson(data, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}


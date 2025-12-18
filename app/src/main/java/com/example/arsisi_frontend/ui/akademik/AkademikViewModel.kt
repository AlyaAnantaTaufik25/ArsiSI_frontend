package com.example.arsisi_frontend.ui.akademik

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import com.example.arsisi_frontend.data.model.AkademikDocument
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AkademikViewModel : ViewModel() {
    private val _documents = mutableStateListOf<AkademikDocument>()
    val documents: List<AkademikDocument> get() = _documents

    fun addDocument(
        title: String, category: String, description: String,
        fileName: String, fileSize: String, fileUri: String,
        // Update tipe data parameter
        attachments: List<Triple<String, String, String>>
    ) {
        val newDoc = AkademikDocument(
            title = title, category = category, description = description, date = getCurrentDate(),
            fileName = fileName, fileSize = fileSize, fileUri = fileUri,
            attachments = attachments
        )
        _documents.add(0, newDoc)
    }

    fun updateDocument(
        id: String, title: String, category: String, description: String,
        fileName: String, fileSize: String, fileUri: String,
        // Update tipe data parameter
        attachments: List<Triple<String, String, String>>
    ) {
        val index = _documents.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldDoc = _documents[index]
            _documents[index] = oldDoc.copy(
                title = title, category = category, description = description,
                fileName = fileName, fileSize = fileSize, fileUri = fileUri,
                attachments = attachments
            )
        }
    }

    fun deleteDocument(id: String) { _documents.removeAll { it.id == id } }
    fun getDocumentById(id: String): AkademikDocument? { return _documents.find { it.id == id } }
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}
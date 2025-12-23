package com.example.arsisi_frontend.ui.akademik

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.arsisi_frontend.data.model.AkademikDocument
import com.example.arsisi_frontend.data.model.Attachment
import com.example.arsisi_frontend.data.repository.AkademikRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AkademikViewModel(
    private val repository: AkademikRepository
) : ViewModel() {
    
    // State untuk semua dokumen
    val documents: StateFlow<List<AkademikDocument>> = repository.getAllDocuments()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // State untuk dokumen yang sedang dilihat/diedit
    private val _currentDocument = mutableStateOf<AkademikDocument?>(null)
    val currentDocument: State<AkademikDocument?> = _currentDocument
    
    // State untuk loading
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    
    // State untuk error
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        // Load semua dokumen saat ViewModel dibuat
        loadDocuments()
    }
    
    fun loadDocuments() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                // Documents akan otomatis diupdate melalui Flow
            } catch (e: Exception) {
                _error.value = "Gagal memuat dokumen: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadDocumentById(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _currentDocument.value = repository.getDocumentByIdSync(id)
            } catch (e: Exception) {
                _error.value = "Gagal memuat dokumen: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addDocument(
        title: String, category: String, description: String,
        fileName: String, fileSize: String, fileUri: String,
        attachments: List<Attachment>
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val newDoc = AkademikDocument(
                    title = title, 
                    category = category, 
                    description = description, 
                    date = getCurrentDate(), // Tanggal pertama kali upload
                    fileName = fileName, 
                    fileSize = fileSize, 
                    fileUri = fileUri,
                    attachments = attachments,
                    updatedAt = null // Dokumen baru belum pernah diupdate
                )
                
                repository.insertDocument(newDoc)
            } catch (e: Exception) {
                _error.value = "Gagal menyimpan dokumen: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateDocument(
        id: String, title: String, category: String, description: String,
        fileName: String, fileSize: String, fileUri: String,
        attachments: List<Attachment>
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val existingDoc = repository.getDocumentByIdSync(id)
                if (existingDoc != null) {
                    val updatedDoc = existingDoc.copy(
                        title = title, 
                        category = category, 
                        description = description,
                        fileName = fileName, 
                        fileSize = fileSize, 
                        fileUri = fileUri,
                        attachments = attachments,
                        updatedAt = getCurrentDate() // Set tanggal update saat edit
                    )
                    repository.updateDocument(updatedDoc)
                }
            } catch (e: Exception) {
                _error.value = "Gagal mengupdate dokumen: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteDocument(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                repository.deleteDocumentById(id)
            } catch (e: Exception) {
                _error.value = "Gagal menghapus dokumen: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getDocumentById(id: String): AkademikDocument? {
        return documents.value.find { it.id == id }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}
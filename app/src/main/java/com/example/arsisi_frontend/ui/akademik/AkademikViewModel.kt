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
    
    // Sync documents from API on init
    init {
        syncDocuments()
    }
    
    fun syncDocuments() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.syncDocuments()
            } catch (e: Exception) {
                _error.value = "Gagal sync: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

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
    
    fun loadDocumentById(id: Int) {
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
        judul: String, kategori: String, deskripsi: String,
        file_name: String, file_size: String, file_path: String,
        attachments: List<Attachment>
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                // Call API to create document with attachments
                val result = repository.createDocument(
                    judul = judul,
                    kategori = kategori,
                    deskripsi = deskripsi,
                    tanggal = getCurrentDate(),
                    fileUri = file_path,
                    attachments = attachments
                )
                
                if (result.isFailure) {
                    _error.value = "Gagal upload: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Gagal menyimpan dokumen: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateDocument(
        id: Int, judul: String, kategori: String, deskripsi: String,
        tanggal: String,  // Add tanggal parameter to preserve original date
        file_name: String, file_size: String, file_path: String,
        attachments: List<Attachment>
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                // Call API to update document
                val result = repository.updateDocument(
                    id = id,
                    judul = judul,
                    kategori = kategori,
                    deskripsi = deskripsi,
                    tanggal = tanggal,  // Use original date, not current date
                    fileUri = if (file_path.startsWith("content://")) file_path else null
                )
                
                if (result.isFailure) {
                    _error.value = "Gagal update: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Gagal mengupdate dokumen: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteDocument(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                // Call API to delete document
                val result = repository.deleteDocument(id)
                
                if (result.isFailure) {
                    _error.value = "Gagal hapus: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Gagal menghapus dokumen: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getDocumentById(id: Int): AkademikDocument? {
        return documents.value.find { it.dokumen_id == id }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
    
    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
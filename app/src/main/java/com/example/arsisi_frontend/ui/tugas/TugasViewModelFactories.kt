package com.example.arsisi_frontend.ui.tugas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.arsisi_frontend.data.repository.TugasRepository
import com.example.arsisi_frontend.data.repository.MataKuliahRepository

class TugasViewModelFactory(
    private val tugasRepository: TugasRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TugasViewModel::class.java)) {
            return TugasViewModel(tugasRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TugasFormViewModelFactory(
    private val tugasRepository: TugasRepository,
    private val matakuliahRepository: MataKuliahRepository,
    private val tugasId: Int? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TugasFormViewModel::class.java)) {
            return TugasFormViewModel(tugasRepository, matakuliahRepository, tugasId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TugasDetailViewModelFactory(
    private val repository: TugasRepository,
    private val tugasId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TugasDetailViewModel::class.java)) {
            return TugasDetailViewModel(repository, tugasId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
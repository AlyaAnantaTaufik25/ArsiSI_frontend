package com.example.arsisi_frontend.ui.akademik

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.arsisi_frontend.data.repository.AkademikRepository

class AkademikViewModelFactory(
    private val repository: AkademikRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AkademikViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AkademikViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}





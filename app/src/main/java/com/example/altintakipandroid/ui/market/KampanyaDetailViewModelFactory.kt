package com.example.altintakipandroid.ui.market

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class KampanyaDetailViewModelFactory(
    private val application: Application,
    private val slideId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return KampanyaDetailViewModel(application, slideId) as T
    }
}

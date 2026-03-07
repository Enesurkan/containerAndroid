package com.example.altintakipandroid.ui.market

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProductListViewModelFactory(
    private val application: Application,
    private val categoryId: Int,
    private val subcategoryId: Int?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductListViewModel(application, categoryId, subcategoryId) as T
    }
}

class ProductDetailViewModelFactory(
    private val application: Application,
    private val productId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductDetailViewModel(application, productId) as T
    }
}

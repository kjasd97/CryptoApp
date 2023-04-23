package com.example.cryptoapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class CoinViewModelFactory @Inject constructor(
    val models:@JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel> >
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       return models[modelClass]?.get() as T
    }
}
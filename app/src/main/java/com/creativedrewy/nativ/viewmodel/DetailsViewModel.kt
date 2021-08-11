package com.creativedrewy.nativ.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.creativedrewy.nativ.viewstate.ViewStateCache
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val viewStateCache: ViewStateCache
) : ViewModel() {

    fun loadNftDetails(id: String) {
        viewStateCache.cachedProps.firstOrNull { it.hashCode().toString() == id }?.let {
            Log.v("SOL", "You have found your item! ${ it.name }")
        }
    }
}
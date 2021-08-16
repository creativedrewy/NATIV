package com.creativedrewy.nativ.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.creativedrewy.nativ.viewstate.ViewStateCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val viewStateCache: ViewStateCache
) : ViewModel() {

    val viewState: StateFlow<NftViewProps>
        get() = _state

    private val _state = MutableStateFlow<NftViewProps>(NftViewProps())

    fun loadNftDetails(id: String) {
        viewStateCache.cachedProps.firstOrNull { it.hashCode().toString() == id }?.let {
            _state.value = it
        }
    }
}

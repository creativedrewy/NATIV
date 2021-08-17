package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import com.creativedrewy.nativ.viewstate.ViewStateCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class ScreenState

class Ready(
    val props: NftViewProps
): ScreenState()

object NotReady : ScreenState()

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val viewStateCache: ViewStateCache
) : ViewModel() {

    val viewState: StateFlow<ScreenState>
        get() = _state

    private val _state = MutableStateFlow<ScreenState>(NotReady)

    fun loadNftDetails(id: String) {
        viewStateCache.cachedProps.firstOrNull { it.hashCode().toString() == id }?.let {
            _state.value = Ready(it)
        }
    }
}
package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.usecase.WallpaperCatalogUseCase
import com.creativedrewy.solananft.usecase.FavoriteNftUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectWallpaperViewModel @Inject constructor(
    private val wallpaperCatalogUseCase: WallpaperCatalogUseCase,
    private val favoriteNftUseCase: FavoriteNftUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SelectWallpaperViewState())
    val viewState: StateFlow<SelectWallpaperViewState> get() = _state

    fun loadWallpapers() {
        viewModelScope.launch {
            val favoriteCount = favoriteNftUseCase.getAllFavorites().size

            val wallpapers = wallpaperCatalogUseCase.getAvailableWallpapers().map { def ->
                WallpaperViewProps(
                    name = def.name,
                    previewImageRes = def.previewImageRes,
                    requiredFavorites = def.requiredFavorites,
                    purchaseId = def.purchaseId,
                    serviceClass = def.serviceClass,
                    isEnabled = favoriteCount >= def.requiredFavorites
                )
            }

            _state.value = SelectWallpaperViewState(wallpapers = wallpapers)
        }
    }
}

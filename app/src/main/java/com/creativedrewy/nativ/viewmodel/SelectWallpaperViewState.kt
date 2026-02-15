package com.creativedrewy.nativ.viewmodel

data class WallpaperViewProps(
    val name: String,
    val previewImageRes: Int,
    val requiredFavorites: Int,
    val purchaseId: String,
    val serviceClass: Class<*>,
    val isEnabled: Boolean = false
)

data class SelectWallpaperViewState(
    val wallpapers: List<WallpaperViewProps> = emptyList()
)

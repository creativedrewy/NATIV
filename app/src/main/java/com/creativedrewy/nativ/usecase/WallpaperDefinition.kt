package com.creativedrewy.nativ.usecase

import androidx.annotation.DrawableRes

data class WallpaperDefinition(
    val name: String,
    @DrawableRes val previewImageRes: Int,
    val requiredFavorites: Int,
    val purchaseId: String,
    val serviceClass: Class<*>
)

package com.creativedrewy.nativ.ethereumnft

data class OpenSeaResultsDto(
    val assets: List<OpenSeaAssetDetails>
)

data class OpenSeaAssetDetails(
    val image_preview_url: String?,
    val image_thumbnail_url: String?,
    val animation_url: String?,
    val name: String?,
    val description: String?,
    val external_link: String?,
    val traits: List<OpenSeaTrait>?
)

data class OpenSeaTrait(
    val trait_type: String,
    val value: String
)
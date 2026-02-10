package com.creativedrewy.nativ.viewmodel

import java.util.UUID

// Screen status state --------------------------------------------------

sealed class NftGalleryViewState(
    val listItems: List<NftViewProps>
)

class Empty : NftGalleryViewState(listOf())

class Loading(
    private val items: List<NftViewProps> = listOf()
) : NftGalleryViewState(items)

class Completed(
    val items: List<NftViewProps>
) : NftGalleryViewState(items)

data class Display(
    val items: List<NftViewProps>
) : NftGalleryViewState(items)

// :::::::: Visual properties to render Nft in the UI ::::::::

data class NftViewProps(
    val id: UUID,
    val name: String = "",
    val description: String = "",
    val blockchain: Blockchain = Blockchain(),
    val siteUrl: String = "",
    val displayImageUrl: String = "",
    val videoUrl: String = "",
    val assetType: AssetType = Image,
    val assetUrl: String = "",
    val attributes: List<Attribute> = listOf(),
    val isPending: Boolean = true
)

data class Attribute(
    val name: String,
    val value: String
)

sealed class AssetType

object Model3d : AssetType()
object Image : AssetType()
object AnimatedImage : AssetType()
object ImageAndVideo : AssetType()

class Blockchain(
    val ticker: String = "",
    val logoRes: Int = -1
)

package com.creativedrewy.nativ.viewmodel

import com.creativedrewy.solananft.viewmodel.NftViewProps

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

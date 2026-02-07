package com.creativedrewy.nativ.viewmodel

data class CollectionNftViewProps(
    val assetId: String,
    val name: String,
    val imageUrl: String
)

sealed class CollectionDetailViewState {
    object Loading : CollectionDetailViewState()
    data class Display(
        val collectionName: String,
        val nfts: List<CollectionNftViewProps>
    ) : CollectionDetailViewState()
}

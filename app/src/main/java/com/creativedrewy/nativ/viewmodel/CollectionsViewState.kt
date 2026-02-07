package com.creativedrewy.nativ.viewmodel

data class CollectionViewProps(
    val collectionId: String,
    val collectionName: String,
    val previewImageUrl: String,
    val nftCount: Int
)

sealed class CollectionsViewState {
    object Empty : CollectionsViewState()
    object Loading : CollectionsViewState()
    data class Display(
        val collections: List<CollectionViewProps>,
        val searchQuery: String = "",
        val isSearching: Boolean = false
    ) : CollectionsViewState()
}

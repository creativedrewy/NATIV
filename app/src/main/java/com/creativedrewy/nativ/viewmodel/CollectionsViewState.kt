package com.creativedrewy.nativ.viewmodel

data class CollectionViewProps(
    val collectionId: String,
    val collectionName: String,
    val previewImageUrl: String,
    val nftCount: Int
)

data class FavoriteNftViewProps(
    val tokenAddress: String,
    val name: String,
    val imageUrl: String
)

sealed class CollectionsViewState {
    object Empty : CollectionsViewState()
    object Loading : CollectionsViewState()
    data class Display(
        val collections: List<CollectionViewProps>,
        val favorites: List<FavoriteNftViewProps> = emptyList(),
        val searchQuery: String = "",
        val isSearching: Boolean = false,
        val isRefreshing: Boolean = false
    ) : CollectionsViewState()
}

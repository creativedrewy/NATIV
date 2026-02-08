package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.chainsupport.ISupportedChains
import com.creativedrewy.nativ.chainsupport.findLoaderByTicker
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
import com.creativedrewy.solananft.usecase.CollectionDisplayInfo
import com.creativedrewy.solananft.usecase.CollectionsUseCase
import com.creativedrewy.solananft.usecase.FavoriteNftUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val userAddrsUseCase: UserAddressesUseCase,
    private val collectionsUseCase: CollectionsUseCase,
    private val favoriteNftUseCase: FavoriteNftUseCase,
    private val chainSupport: ISupportedChains
) : ViewModel() {

    private val _state = MutableStateFlow<CollectionsViewState>(CollectionsViewState.Empty)
    val viewState: StateFlow<CollectionsViewState> get() = _state

    private var allCollections: List<CollectionViewProps> = emptyList()
    private var allFavorites: List<FavoriteNftViewProps> = emptyList()
    private var currentQuery: String = ""

    fun loadCollections() {
        viewModelScope.launch {
            loadFavorites()

            val cachedCollections = collectionsUseCase.loadCollections()
            if (cachedCollections.isNotEmpty()) {
                // Show cached data immediately; no API call needed
                applyCollections(cachedCollections)
            } else {
                // No cached data — fetch from API on first load
                _state.value = CollectionsViewState.Loading
                syncNftsFromApi()
                refreshCollectionsFromDb()
            }
        }
    }

    fun reloadCollections() {
        viewModelScope.launch {
            // Keep showing current collections while refreshing in the background
            val current = _state.value
            if (current is CollectionsViewState.Display) {
                _state.value = current.copy(isRefreshing = true)
            } else {
                _state.value = CollectionsViewState.Loading
            }

            loadFavorites()
            syncNftsFromApi()
            refreshCollectionsFromDb()
        }
    }

    fun onSearchQueryChanged(query: String) {
        currentQuery = query
        viewModelScope.launch {
            if (query.isBlank()) {
                _state.value = CollectionsViewState.Display(
                    collections = allCollections,
                    favorites = allFavorites,
                    searchQuery = query,
                    isSearching = false
                )
            } else {
                val matchingIds = collectionsUseCase.searchCollections(query)
                val filtered = allCollections.filter { it.collectionId in matchingIds }
                _state.value = CollectionsViewState.Display(
                    collections = filtered,
                    favorites = allFavorites,
                    searchQuery = query,
                    isSearching = true
                )
            }
        }
    }

    /**
     * Trigger the blockchain loaders to sync NFTs from the API into the local database.
     */
    private suspend fun syncNftsFromApi() {
        val userAddresses = userAddrsUseCase.loadUserAddresses()

        val loadingFlows = userAddresses.map { chainAddr ->
            val chain = chainSupport.supportedChains.toTypedArray().find { it.ticker == chainAddr.blockchain }
                ?: throw IllegalArgumentException("Could not find a supported chain match with db address")

            val nftLoader = chainSupport.findLoaderByTicker(chainAddr.blockchain)
                ?: throw IllegalArgumentException("Could not find a loader for blockchain type")

            nftLoader.loadNftsThenMetaForAddress(chain, chainAddr.pubKey)
        }

        // Collect all emissions to completion — we don't need the intermediate states here,
        // we just need the side-effect of the loaders populating the database.
        loadingFlows.merge()
            .onCompletion { /* sync done */ }
            .collect { /* consuming emissions to drive the flow */ }
    }

    private suspend fun refreshCollectionsFromDb() {
        val collections = collectionsUseCase.loadCollections()
        applyCollections(collections)
    }

    private suspend fun loadFavorites() {
        val favorites = favoriteNftUseCase.getAllFavorites()
        allFavorites = favorites.map { fav ->
            FavoriteNftViewProps(
                tokenAddress = fav.tokenAddress,
                name = fav.name,
                imageUrl = fav.imageUrl
            )
        }
    }

    private fun applyCollections(collections: List<CollectionDisplayInfo>) {
        allCollections = collections.map { info ->
            CollectionViewProps(
                collectionId = info.collectionId,
                collectionName = info.collectionName,
                previewImageUrl = info.previewImageUrl,
                nftCount = info.nftCount
            )
        }.sortedByDescending { it.nftCount }

        // Re-apply any active search filter
        if (currentQuery.isBlank()) {
            _state.value = CollectionsViewState.Display(
                collections = allCollections,
                favorites = allFavorites,
                searchQuery = currentQuery,
                isSearching = false
            )
        } else {
            onSearchQueryChanged(currentQuery)
        }
    }
}

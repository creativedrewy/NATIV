package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.chainsupport.ISupportedChains
import com.creativedrewy.nativ.chainsupport.findLoaderByTicker
import com.creativedrewy.nativ.usecase.CollectionsUseCase
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
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
    private val chainSupport: ISupportedChains
) : ViewModel() {

    private val _state = MutableStateFlow<CollectionsViewState>(CollectionsViewState.Empty)
    val viewState: StateFlow<CollectionsViewState> get() = _state

    private var allCollections: List<CollectionViewProps> = emptyList()
    private var currentQuery: String = ""

    fun loadCollections() {
        viewModelScope.launch {
            _state.value = CollectionsViewState.Loading

            // Trigger the NFT sync via the existing chain loader mechanism
            syncNftsFromApi()

            // After sync, load collections from the database
            refreshCollectionsFromDb()
        }
    }

    fun reloadCollections() {
        viewModelScope.launch {
            _state.value = CollectionsViewState.Loading
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
                    searchQuery = query,
                    isSearching = false
                )
            } else {
                val matchingIds = collectionsUseCase.searchCollections(query)
                val filtered = allCollections.filter { it.collectionId in matchingIds }
                _state.value = CollectionsViewState.Display(
                    collections = filtered,
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

        allCollections = collections.map { info ->
            CollectionViewProps(
                collectionId = info.collectionId,
                collectionName = info.collectionName,
                previewImageUrl = info.previewImageUrl,
                nftCount = info.nftCount
            )
        }.sortedBy { it.collectionName.lowercase() }

        // Re-apply any active search filter
        if (currentQuery.isBlank()) {
            _state.value = CollectionsViewState.Display(
                collections = allCollections,
                searchQuery = currentQuery,
                isSearching = false
            )
        } else {
            onSearchQueryChanged(currentQuery)
        }
    }
}

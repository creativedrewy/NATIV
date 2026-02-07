package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.usecase.CollectionNftsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    private val collectionNftsUseCase: CollectionNftsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CollectionDetailViewState>(CollectionDetailViewState.Loading)
    val viewState: StateFlow<CollectionDetailViewState> get() = _state

    fun loadCollection(collectionId: String) {
        viewModelScope.launch {
            _state.value = CollectionDetailViewState.Loading

            val nfts = collectionNftsUseCase.loadNftsForCollection(collectionId)
            val collectionName = collectionNftsUseCase.getCollectionName(collectionId)
                ?: (collectionId.take(8) + "...")

            val nftProps = nfts.map { nft ->
                CollectionNftViewProps(
                    assetId = nft.assetId,
                    name = nft.name,
                    imageUrl = nft.imageUrl
                )
            }

            _state.value = CollectionDetailViewState.Display(
                collectionName = collectionName,
                nfts = nftProps
            )
        }
    }
}

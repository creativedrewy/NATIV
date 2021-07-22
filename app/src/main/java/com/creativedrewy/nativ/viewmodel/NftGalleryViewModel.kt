package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.chainsupport.ISupportedChains
import com.creativedrewy.nativ.chainsupport.findLoaderByTicker
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
import com.creativedrewy.nativ.viewstate.GalleryViewStateMapping
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NftGalleryViewModel @Inject constructor(
    private val userAddrsUseCase: UserAddressesUseCase,
    private val chainSupport: ISupportedChains,
    private val viewStateMapping: GalleryViewStateMapping
): ViewModel() {

    private val _state = MutableStateFlow<NftGalleryViewState>(Empty())

    val viewState: StateFlow<NftGalleryViewState>
        get() = _state

    init {
        loadNfts()
    }

    fun loadNfts() {
        _state.value = Loading()

        viewModelScope.launch {
            userAddrsUseCase.allUserAddresses
                .collect { addresses ->
                    val allNfts = mutableListOf<NftViewProps>()

                    addresses.forEach { chainAddr ->
                        val nftLoader = chainSupport.findLoaderByTicker(chainAddr.blockchain)
                        val nftData = nftLoader?.loadNftsForAddress(chainAddr.pubKey)

                        val nftProps = nftData?.map { viewStateMapping.mapNftMetaToViewState(it) }?.awaitAll()

                        allNfts.addAll(nftProps.orEmpty())
                    }

                    _state.value = Display(allNfts)
                }
        }
    }

}
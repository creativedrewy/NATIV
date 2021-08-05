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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NftGalleryViewModel @Inject constructor(
    private val userAddrsUseCase: UserAddressesUseCase,
    private val chainSupport: ISupportedChains,
    private val viewStateMapping: GalleryViewStateMapping
) : ViewModel() {

    private val _state = MutableStateFlow<NftGalleryViewState>(Empty())

    val viewState: StateFlow<NftGalleryViewState>
        get() = _state

    private var cachedAddrCount = 0
    private var cachedNfts: List<NftViewProps>? = null

    fun loadNfts() {
        _state.value = Loading()

        viewModelScope.launch {
            val addrCount = userAddrsUseCase.loadUserAddresses().size

            if (addrCount == cachedAddrCount && cachedNfts != null) {
                cachedNfts?.let { _state.value = Display(it) }
            } else {
                loadFromAddresses()
            }
        }
    }

    fun reloadNfts() {
        cachedAddrCount = 0
        cachedNfts = null

        loadNfts()
    }

    private suspend fun loadFromAddresses() {
        var allNfts = mutableListOf<NftViewProps>()

        val userAddresses = userAddrsUseCase.loadUserAddresses()
        userAddresses.forEach { chainAddr ->
            val chain = chainSupport.supportedChains.toTypedArray().find { it.ticker == chainAddr.blockchain }
                ?: throw IllegalArgumentException("Could not find a supported chain match with db address; this shouldn't happen")

            val nftLoader = chainSupport.findLoaderByTicker(chainAddr.blockchain)
            val nftData = nftLoader?.loadNftsForAddress(chainAddr.pubKey)

            val nftProps = nftData?.map { viewStateMapping.mapNftMetaToViewState(it, chain) }?.awaitAll()

            allNfts.addAll(nftProps.orEmpty())
        }

        allNfts = allNfts.sortedBy { it.name }.toMutableList()

        cachedAddrCount = userAddresses.size
        cachedNfts = allNfts

        _state.value = Display(allNfts)
    }
}

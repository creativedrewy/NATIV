package com.creativedrewy.nativ.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.chainsupport.ISupportedChains
import com.creativedrewy.nativ.chainsupport.findLoaderByTicker
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
import com.creativedrewy.nativ.viewstate.GalleryViewStateMapping
import com.creativedrewy.nativ.viewstate.ViewStateCache
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
    private val viewStateMapping: GalleryViewStateMapping,
    private val viewStateCache: ViewStateCache
) : ViewModel() {

    private val _state = MutableStateFlow<NftGalleryViewState>(Empty())

    val viewState: StateFlow<NftGalleryViewState>
        get() = _state

    private var cachedAddrCount = 0

    fun loadNfts() {
        _state.value = Loading()

        viewModelScope.launch {
            val addrCount = userAddrsUseCase.loadUserAddresses().size

//            if (addrCount == cachedAddrCount && viewStateCache.hasCache) {
//                cachedNfts?.let { _state.value = Display(it) }
//            } else {
//                loadFromAddresses()
//            }
            val props = listOf(
                NftViewProps(
                    name = "This is a TEMP item",
                    blockchain = Blockchain("TMP", R.drawable.solana_logo)
                )
            )
            viewStateCache.updateCache(props)

            _state.value = Display(props)
        }
    }

    fun reloadNfts() {
        cachedAddrCount = 0
        viewStateCache.clearCache()

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
        viewStateCache.updateCache(allNfts)

        _state.value = Display(allNfts)
    }
}

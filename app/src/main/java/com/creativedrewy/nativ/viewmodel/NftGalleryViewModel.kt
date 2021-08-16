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

    private var cachedAddrCount = 0
    private val _state = MutableStateFlow<NftGalleryViewState>(Empty())

    val viewState: StateFlow<NftGalleryViewState>
        get() = _state

    fun loadNfts() {
        _state.value = Loading()

        viewModelScope.launch {
            val addrCount = userAddrsUseCase.loadUserAddresses().size

//            if (addrCount == cachedAddrCount && viewStateCache.hasCache) {
//                _state.value = Display(viewStateCache.cachedProps)
//            } else {
//                loadFromAddresses()
//            }
            val props = listOf(
                NftViewProps(
                    name = "Corgi #3252",
                    description = "100% Purebred Crypto Corgi. Attributes derived from block number 12826863. It was number 3252 to be claimed.",
                    assetUrl = "https://lh3.googleusercontent.com/oBwn9vxrvZwQfWYdOqAfMx25Y-NeHjE-lTc8SVRqOe245gtPxC56iOBdZIW5DHezi2cXNFHwEsA0qoqTwtJj1hu4zimTHNBY-i_S1A=s250",
                    blockchain = Blockchain("SOL", R.drawable.solana_logo),
                    siteUrl = "www.solanimals.com",
                    attributes = listOf(
                        Attribute("Eye Color", "Blue"),
                        Attribute("Coat", "Orange"),
                        Attribute("Border", "Green"),
                        Attribute("Face Covering", "Laser mask"),
                        Attribute("Hat", "Golden Crown"),
                    )
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

package com.creativedrewy.nativ.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.chainsupport.ISupportedChains
import com.creativedrewy.nativ.chainsupport.findLoaderByTicker
import com.creativedrewy.nativ.chainsupport.nft.MetaLoaded
import com.creativedrewy.nativ.chainsupport.nft.Pending
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
import com.creativedrewy.nativ.viewstate.GalleryViewStateMapping
import com.creativedrewy.nativ.viewstate.ViewStateCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
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
        viewModelScope.launch {
            val addrCount = userAddrsUseCase.loadUserAddresses().size

            if (addrCount == cachedAddrCount && viewStateCache.hasCache) {
                _state.value = Display(viewStateCache.cachedProps)
            } else {
                loadFromAddresses()
            }
        }
    }

    fun reloadNfts() {
        cachedAddrCount = 0
        viewStateCache.clearCache()

        loadNfts()
    }

    private suspend fun loadFromAddresses() {
        _state.value = Loading()

        var allNfts = mutableStateListOf<NftViewProps>()

        val nftMap = mutableMapOf<String, NftViewProps>()

        val userAddresses = userAddrsUseCase.loadUserAddresses()
        userAddresses.forEach { chainAddr ->
            val chain = chainSupport.supportedChains.toTypedArray().find { it.ticker == chainAddr.blockchain }
                ?: throw IllegalArgumentException("Could not find a supported chain match with db address; this shouldn't happen")

            val nftLoader = chainSupport.findLoaderByTicker(chainAddr.blockchain) ?: throw IllegalArgumentException("Could not find a loader for blockchain type")
            //val nftData = nftLoader?.loadNftsForAddress(chainAddr.pubKey)
            nftLoader.loadNftsThenMetaForAddress(chainAddr.pubKey).collect { uriMetaMap ->
                uriMetaMap.keys.forEach { uriKey ->

                    val metaResult = uriMetaMap[uriKey] ?: throw IllegalArgumentException("You must include a meta result with all nft Uris")
                    val viewProp = when (metaResult) {
                        is MetaLoaded -> {
                            val props = nftMap[uriKey] ?: throw IllegalArgumentException("")

                            viewStateMapping.updateNftMetaIntoViewState(props, metaResult.metadata, chain)
                        }
                        Pending -> {
                            viewStateMapping.createPendingNftViewProps(chain)
                        }
                    }

                    nftMap[uriKey] = viewProp

                    val dispNfts = nftMap.values
                        .toMutableList()
                        .sortedBy { it.name.lowercase(Locale.getDefault()) }
                        .toMutableStateList()

                    _state.value = Display(dispNfts)
                }

//                allNfts = allNfts
//                    .toMutableList()
//                    .sortedBy { it.name.lowercase(Locale.getDefault()) }
//                    .toMutableStateList()

                //cachedAddrCount = userAddresses.size
                //viewStateCache.updateCache(allNfts)

                //_state.value = Display(allNfts)
            }

            //val nftProps = nftData?.map { viewStateMapping.mapNftMetaToViewState(it, chain) }?.awaitAll()

            //allNfts.addAll(nftProps.orEmpty())
        }

//        delay(1000)
//
//        val updateNft = allNfts[0].copy(name = "BLAH BLAH BLAH")
//        allNfts[0] = updateNft
//
//        _state.value = Display(allNfts)
    }
}


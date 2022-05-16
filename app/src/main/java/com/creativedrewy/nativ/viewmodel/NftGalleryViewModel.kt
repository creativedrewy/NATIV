package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.chainsupport.ISupportedChains
import com.creativedrewy.nativ.chainsupport.findLoaderByTicker
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
import com.creativedrewy.nativ.viewstate.GalleryViewStateMapping
import com.creativedrewy.nativ.viewstate.ViewStateCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
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

        val nftMap = mutableMapOf<String, NftViewProps>()

        val userAddresses = userAddrsUseCase.loadUserAddresses()
        val loadingFlows = userAddresses.map { chainAddr ->
            val chain = chainSupport.supportedChains.toTypedArray().find { it.ticker == chainAddr.blockchain }
                ?: throw IllegalArgumentException("Could not find a supported chain match with db address; this shouldn't happen")

            val nftLoader = chainSupport.findLoaderByTicker(chainAddr.blockchain) ?: throw IllegalArgumentException("Could not find a loader for blockchain type")

            //TODO: This is a lame way to do this; refactor with multibindings
            nftLoader.loadNftsThenMetaForAddress(chain, chainAddr.pubKey)
        }

        loadingFlows.merge()
            .onCompletion {  }
            .collect {

            }
    }
}

//uriMetaMap.keys.forEach { uriKey ->
//    val metaResult = uriMetaMap[uriKey] ?: throw IllegalArgumentException("You must include a meta result with all nft Uris")
//
//    when (metaResult) {
//        is MetaLoaded -> {
//            val viewProp = if (nftMap.containsKey(uriKey)) {
//                val props = nftMap[uriKey]!!    //Bang bang okay as we have just validated this isn't the case
//
//                viewStateMapping.updateNftMetaIntoViewState(props, metaResult.metadata, chain)
//            } else {
//                val pendingProp = viewStateMapping.createPendingNftViewProps(chain)
//                viewStateMapping.updateNftMetaIntoViewState(pendingProp, metaResult.metadata, chain)
//            }
//
//            nftMap[uriKey] = viewProp
//        }
//        Pending -> {
//            val viewProp = viewStateMapping.createPendingNftViewProps(chain)
//            nftMap[uriKey] = viewProp
//        }
//        Invalid -> {
//            nftMap.remove(uriKey)
//        }
//    }
//
//    val dispNfts = nftMap.values
//        .toMutableList()
//        .sortedBy { it.name.lowercase(Locale.getDefault()) }
//        .toMutableStateList()
//
//    cachedAddrCount = userAddresses.size
//    viewStateCache.updateCache(dispNfts.toList())
//
//    _state.value = Display(dispNfts)
//}


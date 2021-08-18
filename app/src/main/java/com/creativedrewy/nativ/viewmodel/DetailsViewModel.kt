package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import com.creativedrewy.nativ.chainsupport.nft.NftCategories
import com.creativedrewy.nativ.chainsupport.nft.NftFileTypes
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import com.creativedrewy.nativ.downloader.AssetDownloadUseCase
import com.creativedrewy.nativ.viewstate.ViewStateCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class ScreenState

class Ready(
    val props: NftViewProps
): ScreenState()

object NotReady : ScreenState()

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val assetDownloadUseCase: AssetDownloadUseCase,
    private val viewStateCache: ViewStateCache
) : ViewModel() {

    val viewState: StateFlow<ScreenState>
        get() = _state

    private val _state = MutableStateFlow<ScreenState>(NotReady)

    fun loadNftDetails(id: String) {
//                val assetBytes = if (shouldDownloadAsset(nft)) {
//                    findDownloadUri(nft.properties)?.let {
//                        assetDownloadUseCase.downloadAsset(it)
//                    } ?: byteArrayOf()
//                } else {
//                    byteArrayOf()
//                }

        viewStateCache.cachedProps.firstOrNull { it.hashCode().toString() == id }?.let {
            _state.value = Ready(it)
        }
    }

    private fun shouldDownloadAsset(nft: NftMetadata): Boolean {
        return nft.properties.category == NftCategories.VR
    }

    private fun findDownloadUri(props: NftProperties): String? {
        return when (props.category) {
            NftCategories.VR -> {
                props.files.firstOrNull { it.type == NftFileTypes.GLB }?.uri
                    ?: props.files.firstOrNull()?.uri
            }
            else -> null //We don't actually know what we want to do in other cases yet
        }
    }
}

package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.downloader.AssetDownloadUseCase
import com.creativedrewy.nativ.viewstate.ViewStateCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ScreenState

class PropsWithMedia(
    val props: NftViewProps,
    val mediaBytes: ByteArray = byteArrayOf()
)

class Ready(
    val item: PropsWithMedia,
    val isLoadingAsset: Boolean = true
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
        viewStateCache.cachedProps.firstOrNull { it.id.toString() == id }?.let { propItem ->
            val shouldDownload = shouldDownloadAsset(propItem, viewStateCache)

            _state.value = Ready(PropsWithMedia(propItem), shouldDownload)

            if (shouldDownload) {
                viewModelScope.launch {
                    val assetBytes = assetDownloadUseCase.downloadAsset(propItem.assetUrl)

                    viewStateCache.cacheMediaItem(propItem.id.toString(), assetBytes)
                    updateViewState(propItem)
                }
            } else {
                updateViewState(propItem)
            }
        }
    }

    private fun updateViewState(nft: NftViewProps) {
        viewStateCache.mediaCache[nft.id.toString()]?.let { mediaBytes ->
            val displayData = PropsWithMedia(nft, mediaBytes)

            _state.value = Ready(displayData, false)
        }
    }

    private fun shouldDownloadAsset(nft: NftViewProps, cache: ViewStateCache): Boolean {
        return nft.assetType is Model3d && !cache.mediaCache.containsKey(nft.id.toString())
    }
}

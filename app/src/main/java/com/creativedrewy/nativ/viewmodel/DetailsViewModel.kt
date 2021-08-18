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

class Ready(
    val props: NftViewProps,
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
            val shouldDownload = shouldDownloadAsset(propItem)

            _state.value = Ready(propItem, shouldDownload)

            if (shouldDownload) {
                viewModelScope.launch {
                    val assetBytes = assetDownloadUseCase.downloadAsset(propItem.assetUrl)

                    val updatedProps = propItem.copy(mediaBytes = assetBytes)
                    viewStateCache.updatePropItem(updatedProps)

                    _state.value = Ready(updatedProps, false)
                }
            }
        }
    }

    private fun shouldDownloadAsset(nft: NftViewProps): Boolean {
        return nft.assetType is Model3d && nft.mediaBytes.isEmpty()
    }
}

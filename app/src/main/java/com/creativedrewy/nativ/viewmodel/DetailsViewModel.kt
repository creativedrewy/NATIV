package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import com.creativedrewy.nativ.downloader.AssetDownloadUseCase
import com.creativedrewy.nativ.viewstate.ViewStateCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        viewStateCache.cachedProps.firstOrNull { it.id.toString() == id }?.let { props ->
            val shouldDownload = shouldDownloadAsset(props)

            _state.value = Ready(props, shouldDownload)

//            if (shouldDownload) {
//                viewModelScope.launch {
//                    val assetBytes = assetDownloadUseCase.downloadAsset(props.assetUrl)
//
//                    val updatedProps = props.copy(mediaBytes = assetBytes)
//                }
//            }
        }
    }

    private fun shouldDownloadAsset(nft: NftViewProps): Boolean {
        return nft.assetType is Model3d && nft.mediaBytes.isEmpty()
    }
}

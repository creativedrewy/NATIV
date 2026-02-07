package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.downloader.AssetDownloadUseCase
import com.creativedrewy.nativ.usecase.CollectionNftsUseCase
import com.creativedrewy.nativ.viewstate.ViewStateCache
import com.creativedrewy.solananft.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
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
    private val collectionNftsUseCase: CollectionNftsUseCase,
    private val viewStateCache: ViewStateCache
) : ViewModel() {

    val viewState: StateFlow<ScreenState>
        get() = _state

    private val _state = MutableStateFlow<ScreenState>(NotReady)

    fun loadNftDetails(id: String) {
        // First try the legacy ViewStateCache (for backward compatibility)
        val cachedProp = viewStateCache.cachedProps.firstOrNull { it.id.toString() == id }

        if (cachedProp != null) {
            loadFromCache(cachedProp)
        } else {
            // Load from database using the DAS asset ID
            loadFromDatabase(id)
        }
    }

    private fun loadFromCache(propItem: NftViewProps) {
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

    private fun loadFromDatabase(assetId: String) {
        viewModelScope.launch {
            val nftInfo = collectionNftsUseCase.loadNft(assetId) ?: return@launch

            val nftProps = NftViewProps(
                id = UUID.nameUUIDFromBytes(assetId.toByteArray()),
                name = nftInfo.name,
                description = nftInfo.description,
                blockchain = Blockchain(
                    ticker = "SOL",
                    logoRes = R.drawable.solana_logo
                ),
                displayImageUrl = nftInfo.imageUrl,
                videoUrl = nftInfo.animationUrl,
                siteUrl = nftInfo.externalUrl,
                assetType = if (nftInfo.animationUrl.endsWith(".mp4")) ImageAndVideo else Image,
                isPending = false
            )

            _state.value = Ready(PropsWithMedia(nftProps), false)
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

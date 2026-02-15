package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.chainsupport.nft.AnimatedImage
import com.creativedrewy.nativ.chainsupport.nft.FileDetails
import com.creativedrewy.nativ.chainsupport.nft.ImageAndVideo
import com.creativedrewy.nativ.chainsupport.nft.Model3d
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import com.creativedrewy.nativ.viewstate.ViewStateCache
import com.creativedrewy.solananft.R
import com.creativedrewy.solananft.usecase.AssetDownloadUseCase
import com.creativedrewy.solananft.usecase.CollectionNftsUseCase
import com.creativedrewy.solananft.usecase.FavoriteNftUseCase
import com.creativedrewy.solananft.viewmodel.Blockchain
import com.creativedrewy.solananft.viewmodel.NftViewProps
import com.creativedrewy.solananft.viewmodel.toNftViewProps
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ScreenState

class PropsWithMedia(
    val props: NftViewProps,
    val mediaBytes: ByteArray = byteArrayOf()
)

class Ready(
    val item: PropsWithMedia,
    val isLoadingAsset: Boolean = true,
    val assetId: String = "",
    val isFavorited: Boolean = false
): ScreenState()

object NotReady : ScreenState()

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val assetDownloadUseCase: AssetDownloadUseCase,
    private val collectionNftsUseCase: CollectionNftsUseCase,
    private val favoriteNftUseCase: FavoriteNftUseCase,
    private val viewStateCache: ViewStateCache
) : ViewModel() {

    val viewState: StateFlow<ScreenState>
        get() = _state

    private val _state = MutableStateFlow<ScreenState>(NotReady)

    private val _snackbarEvent = Channel<String>(Channel.BUFFERED)
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    private var currentAssetId: String = ""

    fun loadNftDetails(id: String) {
        currentAssetId = id

        // First try the legacy ViewStateCache (for backward compatibility)
        val cachedProp = viewStateCache.cachedProps.firstOrNull { it.id.toString() == id }

        if (cachedProp != null) {
            loadFromCache(cachedProp)
        } else {
            // Load from database using the DAS asset ID
            loadFromDatabase(id)
        }
    }

    fun toggleFavorite() {
        val currentState = _state.value as? Ready ?: return
        val props = currentState.item.props

        viewModelScope.launch {
            // Determine the media URL to cache (images or GLB, not videos)
            val mediaUrl = when (props.assetType) {
                is Model3d -> props.assetUrl.ifBlank { props.videoUrl }
                else -> props.displayImageUrl
            }

            val assetTypeStr = when (props.assetType) {
                is Model3d -> "model3d"
                is AnimatedImage -> "animated_image"
                is ImageAndVideo -> "image_and_video"
                else -> "image"
            }

            val isNowFavorited = favoriteNftUseCase.toggleFavorite(
                tokenAddress = currentAssetId,
                name = props.name,
                imageUrl = props.displayImageUrl,
                mediaUrl = mediaUrl,
                assetType = assetTypeStr
            )

            _state.value = Ready(
                item = currentState.item,
                isLoadingAsset = currentState.isLoadingAsset,
                assetId = currentAssetId,
                isFavorited = isNowFavorited
            )

            if (isNowFavorited) {
                _snackbarEvent.send("Added to your wallpaper!")
            }
        }
    }

    private fun loadFromCache(propItem: NftViewProps) {
        val shouldDownload = shouldDownloadAsset(propItem, viewStateCache)

        viewModelScope.launch {
            val isFav = favoriteNftUseCase.isFavorited(currentAssetId)
            _state.value = Ready(PropsWithMedia(propItem), shouldDownload, currentAssetId, isFav)

            if (shouldDownload) {
                val assetBytes = assetDownloadUseCase.downloadAsset(propItem.assetUrl)
                viewStateCache.cacheMediaItem(propItem.id.toString(), assetBytes)
                updateViewState(propItem, isFav)
            } else {
                updateViewState(propItem, isFav)
            }
        }
    }

    private fun loadFromDatabase(assetId: String) {
        viewModelScope.launch {
            val nftInfo = collectionNftsUseCase.loadNft(assetId) ?: return@launch
            val isFav = favoriteNftUseCase.isFavorited(assetId)

            val nftMetadata = NftMetadata(
                name = nftInfo.name,
                symbol = null,
                description = nftInfo.description,
                image = nftInfo.imageUrl,
                animationUrl = nftInfo.animationUrl,
                externalUrl = nftInfo.externalUrl,
                attributes = null,
                properties = NftProperties(
                    category = null,
                    files = nftInfo.fileTypes.map { FileDetails(null, it) },
                    creators = null
                )
            )

            val nftProps = nftMetadata.toNftViewProps(
                assetId = assetId,
                blockchain = Blockchain(
                    ticker = "SOL",
                    logoRes = R.drawable.solana_logo
                )
            )

            if (nftProps.assetType is Model3d) {
                _state.value = Ready(PropsWithMedia(nftProps), true, assetId, isFav)

                val mediaBytes = assetDownloadUseCase.downloadAsset(nftProps.assetUrl)
                _state.value = Ready(PropsWithMedia(nftProps, mediaBytes), false, assetId, isFav)
            } else {
                _state.value = Ready(PropsWithMedia(nftProps), false, assetId, isFav)
            }
        }
    }

    private fun updateViewState(nft: NftViewProps, isFavorited: Boolean) {
        viewStateCache.mediaCache[nft.id.toString()]?.let { mediaBytes ->
            val displayData = PropsWithMedia(nft, mediaBytes)

            _state.value = Ready(displayData, false, currentAssetId, isFavorited)
        }
    }

    private fun shouldDownloadAsset(nft: NftViewProps, cache: ViewStateCache): Boolean {
        return nft.assetType is Model3d && !cache.mediaCache.containsKey(nft.id.toString())
    }
}

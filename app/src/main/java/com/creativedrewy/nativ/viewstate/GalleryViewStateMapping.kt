package com.creativedrewy.nativ.viewstate

import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.chainsupport.nft.NftCategories
import com.creativedrewy.nativ.chainsupport.nft.NftFileTypes
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import com.creativedrewy.nativ.downloader.AssetDownloadUseCase
import com.creativedrewy.nativ.viewmodel.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GalleryViewStateMapping @Inject constructor(
    private val assetDownloadUseCase: AssetDownloadUseCase,
) {

    suspend fun mapNftMetaToViewState(nft: NftMetadata, chain: SupportedChain): Deferred<NftViewProps> {
        return coroutineScope {
            async {
                val assetBytes = if (shouldDownloadAsset(nft)) {
                    findDownloadUri(nft.properties)?.let {
                        assetDownloadUseCase.downloadAsset(it)
                    } ?: byteArrayOf()
                } else {
                    byteArrayOf()
                }

                val chainDetails = Blockchain(chain.ticker, chain.iconRes)

                NftViewProps(
                    name = nft.name,
                    description = nft.description,
                    blockchain = chainDetails,
                    // siteUrl = nft.externalUrl,    //TODO: Not deserializing this properly from metaplex
                    assetType = determineAssetType(nft),
                    assetUrl = determineAssetUrl(nft),
                    mediaBytes = assetBytes
                )
            }
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

    private fun determineAssetType(nft: NftMetadata): AssetType {
        return if (nft.properties.category == NftCategories.VR) {
            Model3d
        } else {
            Image
        }
    }

    private fun determineAssetUrl(nft: NftMetadata): String {
        return if (nft.properties.category != NftCategories.VR) nft.image else ""
    }
}

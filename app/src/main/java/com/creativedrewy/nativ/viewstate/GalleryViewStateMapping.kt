package com.creativedrewy.nativ.viewstate

import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.chainsupport.nft.NftCategories
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import com.creativedrewy.nativ.viewmodel.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GalleryViewStateMapping @Inject constructor() {

    suspend fun mapNftMetaToViewState(nft: NftMetadata, chain: SupportedChain): Deferred<NftViewProps> {
        return coroutineScope {
            async {
                val chainDetails = Blockchain(chain.ticker, chain.iconRes)
                val attribs = nft.attributes?.map {
                    Attribute(
                        name = it.traitType,
                        value = it.value
                    )
                } ?: listOf()

                NftViewProps(
                    name = nft.name,
                    description = nft.description,
                    blockchain = chainDetails,
                    siteUrl = nft.externalUrl,
                    assetType = determineAssetType(nft),
                    assetUrl = nft.image,
                    attributes = attribs,
                    mediaBytes = byteArrayOf()
                )
            }
        }
    }

    private fun determineAssetType(nft: NftMetadata): AssetType {
        return if (nft.properties.category == NftCategories.VR) {
            Model3d
        } else {
            Image
        }
    }
}

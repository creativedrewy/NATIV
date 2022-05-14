package com.creativedrewy.nativ.viewstate

import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.chainsupport.nft.NftCategories
import com.creativedrewy.nativ.chainsupport.nft.NftFileTypes
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import com.creativedrewy.nativ.viewmodel.*
import java.util.*
import javax.inject.Inject

class GalleryViewStateMapping @Inject constructor() {

    fun createPendingNftViewProps(): NftViewProps {
        return NftViewProps(
            id = UUID.randomUUID(),
            isPending = true
        )
    }

    fun updateNftMetaIntoViewState(existingProps: NftViewProps, nft: NftMetadata, chain: SupportedChain): NftViewProps {
        val chainDetails = Blockchain(chain.ticker, chain.iconRes)
        val attribs = nft.attributes?.map {
            Attribute(
                name = it.traitType ?: "",
                value = it.value ?: ""
            )
        } ?: listOf()

        return existingProps.copy(
            name = nft.name ?: "",
            description = nft.description ?: "",
            blockchain = chainDetails,
            siteUrl = nft.externalUrl ?: "",
            displayImageUrl = nft.image ?: "",
            videoUrl = nft.animationUrl ?: "",
            assetType = determineAssetType(nft),
            assetUrl = findDownloadUri(nft.properties) ?: "",
            attributes = attribs,
            mediaBytes = byteArrayOf(),
            isPending = false
        )
    }

    private fun determineAssetType(nft: NftMetadata): AssetType {
        return when {
            nft.properties?.category == NftCategories.VR -> Model3d
            nft.properties?.category == NftCategories.Image
                    && nft.animationUrl?.endsWith(".mp4") == true -> ImageAndVideo
            else -> Image
        }
    }

    private fun findDownloadUri(props: NftProperties?): String? {
        return when (props?.category) {
            NftCategories.VR -> {
                props.files?.firstOrNull { it.type == NftFileTypes.GLB }?.uri
                    ?: props.files?.firstOrNull()?.uri
            }
            else -> null //We don't actually know what we want to do in other cases yet
        }
    }
}

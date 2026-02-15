package com.creativedrewy.nativ.viewstate

import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.chainsupport.nft.AnimatedImage
import com.creativedrewy.nativ.chainsupport.nft.AssetType
import com.creativedrewy.nativ.chainsupport.nft.Image
import com.creativedrewy.nativ.chainsupport.nft.ImageAndVideo
import com.creativedrewy.nativ.chainsupport.nft.Model3d
import com.creativedrewy.nativ.chainsupport.nft.NftCategories
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import com.creativedrewy.nativ.chainsupport.nft.isGifUrl
import com.creativedrewy.nativ.chainsupport.nft.isGlbUrl
import com.creativedrewy.nativ.chainsupport.nft.isMp4Url
import com.creativedrewy.solananft.viewmodel.Attribute
import com.creativedrewy.solananft.viewmodel.Blockchain
import com.creativedrewy.solananft.viewmodel.NftViewProps
import java.util.UUID
import javax.inject.Inject

class GalleryViewStateMapping @Inject constructor() {

    fun createPendingNftViewProps(chain: SupportedChain): NftViewProps {
        val chainDetails = Blockchain(chain.ticker, chain.iconRes)

        return NftViewProps(
            id = UUID.randomUUID(),
            blockchain = chainDetails,
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
            assetUrl = findDownloadUri(nft, nft.properties) ?: "",
            attributes = attribs,
            isPending = false
        )
    }

    private fun determineAssetType(nft: NftMetadata): AssetType {
        val animUrl = nft.animationUrl ?: ""
        val imageUrl = nft.image ?: ""
        return when {
            nft.properties?.category == NftCategories.VR -> Model3d
            isGlbUrl(animUrl) -> Model3d
            isMp4Url(animUrl) -> ImageAndVideo
            nft.properties?.category == NftCategories.Gif -> AnimatedImage
            isGifUrl(animUrl) || isGifUrl(imageUrl) -> AnimatedImage
            else -> Image
        }
    }

    private fun findDownloadUri(nft: NftMetadata, props: NftProperties?): String? {
        // For 3D models: prefer GLB file from properties, fall back to animation URL
        if (props?.category == NftCategories.VR || isGlbUrl(nft.animationUrl ?: "")) {
            return props?.files?.firstOrNull {
                val type = it.type?.lowercase() ?: ""
                type == "model/gltf-binary" || type.contains("gltf") || it.uri?.lowercase()?.contains(".glb") == true
            }?.uri
                ?: props?.files?.firstOrNull()?.uri
                ?: nft.animationUrl
        }
        return null
    }
}

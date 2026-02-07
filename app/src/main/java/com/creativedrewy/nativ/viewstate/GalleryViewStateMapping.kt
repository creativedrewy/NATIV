package com.creativedrewy.nativ.viewstate

import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.chainsupport.nft.NftCategories
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import com.creativedrewy.nativ.viewmodel.AssetType
import com.creativedrewy.nativ.viewmodel.Attribute
import com.creativedrewy.nativ.viewmodel.Blockchain
import com.creativedrewy.nativ.viewmodel.Image
import com.creativedrewy.nativ.viewmodel.ImageAndVideo
import com.creativedrewy.nativ.viewmodel.Model3d
import com.creativedrewy.nativ.viewmodel.NftViewProps
import com.creativedrewy.nativ.viewmodel.isGlbUrl
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
        return when {
            nft.properties?.category == NftCategories.VR -> Model3d
            isGlbUrl(animUrl) -> Model3d
            animUrl.endsWith(".mp4") -> ImageAndVideo
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

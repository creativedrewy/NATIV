package com.creativedrewy.solananft.viewmodel

import com.creativedrewy.nativ.chainsupport.nft.AssetType
import com.creativedrewy.nativ.chainsupport.nft.Image
import com.creativedrewy.nativ.chainsupport.nft.NftCategories
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import com.creativedrewy.nativ.chainsupport.nft.determineAssetType
import com.creativedrewy.nativ.chainsupport.nft.isGlbUrl
import java.util.UUID

data class NftViewProps(
    val id: UUID,
    val name: String = "",
    val description: String = "",
    val blockchain: Blockchain = Blockchain(),
    val siteUrl: String = "",
    val displayImageUrl: String = "",
    val videoUrl: String = "",
    val assetType: AssetType = Image,
    val assetUrl: String = "",
    val attributes: List<Attribute> = listOf(),
    val isPending: Boolean = true
)

data class Attribute(
    val name: String,
    val value: String
)

class Blockchain(
    val ticker: String = "",
    val logoRes: Int = -1
)

/**
 * Map an [NftMetadata] into a fully resolved [NftViewProps].
 */
fun NftMetadata.toNftViewProps(
    assetId: String,
    blockchain: Blockchain = Blockchain()
): NftViewProps {
    val assetType = determineAssetType()
    val animUrl = animationUrl ?: ""
    val attribs = attributes?.map {
        Attribute(
            name = it.traitType ?: "",
            value = it.value ?: ""
        )
    } ?: listOf()

    return NftViewProps(
        id = UUID.nameUUIDFromBytes(assetId.toByteArray()),
        name = name ?: "",
        description = description ?: "",
        blockchain = blockchain,
        siteUrl = externalUrl ?: "",
        displayImageUrl = image ?: "",
        videoUrl = animUrl,
        assetType = assetType,
        assetUrl = findDownloadUri(this, properties) ?: "",
        attributes = attribs,
        isPending = false
    )
}

private fun findDownloadUri(nft: NftMetadata, props: NftProperties?): String? {
    val animUrl = nft.animationUrl ?: ""
    if (props?.category == NftCategories.VR || isGlbUrl(animUrl)) {
        return props?.files?.firstOrNull {
            val type = it.type?.lowercase() ?: ""
            type == "model/gltf-binary" || type.contains("gltf") || it.uri?.lowercase()?.contains(".glb") == true
        }?.uri
            ?: props?.files?.firstOrNull()?.uri
            ?: nft.animationUrl
    }
    return null
}

package com.creativedrewy.solananft.usecase

import com.creativedrewy.nativ.chainsupport.nft.FileDetails
import com.creativedrewy.nativ.chainsupport.nft.NftAttributes
import com.creativedrewy.nativ.chainsupport.nft.NftCategories
import com.creativedrewy.nativ.chainsupport.nft.NftCreator
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import com.creativedrewy.solananft.dto.DasAsset
import com.creativedrewy.solananft.repository.NftAssetRepository
import javax.inject.Inject

/**
 * Maps a [DasAsset] to [NftMetadata]. Shared by [MetaplexNftUseCase] and any other
 * code that needs to resolve DAS data into the canonical metadata format.
 */
class NftMetadataMapper @Inject constructor() {

    fun mapToNftMetadata(asset: DasAsset): NftMetadata {
        val content = asset.content ?: throw IllegalArgumentException("Asset has no content")
        val meta = content.metadata

        val attributes = meta.attributes?.map {
            NftAttributes(it.traitType, it.value, 0)
        } ?: emptyList()

        val animationUrl = content.links?.get("animation_url") ?: ""
        val hasGlbFile = content.files?.any { file ->
            val type = file.type?.lowercase() ?: ""
            type == "model/gltf-binary" || type == "model/gltf+json" || type.contains("gltf")
                    || file.uri?.lowercase()?.let { it.contains(".glb") || it.contains("ext=glb") } == true
        } == true
        val hasGlbAnimUrl = animationUrl.lowercase().let { it.contains(".glb") || it.contains("ext=glb") }

        val imageUrl = content.links?.get("image") ?: ""
        val hasGifFile = content.files?.any { file ->
            val type = file.type?.lowercase() ?: ""
            type == "image/gif" || file.uri?.lowercase()?.let { it.contains(".gif") || it.contains("ext=gif") } == true
        } == true
        val hasGifImageUrl = imageUrl.lowercase().let { it.contains(".gif") || it.contains("ext=gif") }
        val hasGifAnimUrl = animationUrl.lowercase().let { it.contains(".gif") || it.contains("ext=gif") }

        val category = when {
            hasGlbFile || hasGlbAnimUrl -> NftCategories.VR
            hasGifFile || hasGifImageUrl || hasGifAnimUrl -> NftCategories.Gif
            else -> NftCategories.Image
        }

        val properties = NftProperties(
            category = category,
            files = content.files?.map { FileDetails(it.uri, it.type) },
            creators = asset.creators?.map { NftCreator(it.address) }
        )

        return NftMetadata(
            meta.name,
            meta.symbol,
            meta.description,
            content.links?.get("image"),
            content.links?.get("animation_url"),
            content.links?.get("external_url"),
            attributes,
            properties
        )
    }

    /**
     * Load a single NFT's [NftMetadata] by its asset ID from the local database.
     * Returns null if the asset is not cached or has no content.
     */
    suspend fun loadNftMetadataById(
        assetId: String,
        nftAssetRepository: NftAssetRepository
    ): NftMetadata? {
        val dasAsset = nftAssetRepository.getAssetById(assetId) ?: return null
        return try {
            mapToNftMetadata(dasAsset)
        } catch (_: Exception) {
            null
        }
    }
}

package com.creativedrewy.nativ.chainsupport.nft

sealed class NftMetaStatus

object Pending : NftMetaStatus()
object Invalid : NftMetaStatus()

class MetaLoaded(
    val metadata: NftMetadata
) : NftMetaStatus()

data class NftMetadata(
    val name: String?,
    val symbol: String?,
    val description: String?,
    val image: String?,
    val animationUrl: String?,
    val externalUrl: String?,
    val attributes: List<NftAttributes>?,
    val properties: NftProperties?
)

data class NftAttributes(
    val traitType: String?,
    val value: String?,
    val traitCount: Int = 0
)

object NftCategories {
    const val VR = "vr"
    const val Image = "image"
    const val Gif = "gif"
}

data class NftProperties(
    val category: String?,
    val files: List<FileDetails>?,
    val creators: List<NftCreator>?
)

object NftFileTypes {
    const val GLB = "glb"
}

data class FileDetails(
    val uri: String?,
    val type: String?
)

data class NftCreator(
    val address: String?,
)

// ========== Asset type detection extensions ==========

sealed class AssetType

object Model3d : AssetType()
object Image : AssetType()
object AnimatedImage : AssetType()
object ImageAndVideo : AssetType()

/**
 * Determine the [AssetType] for this metadata by inspecting the animation URL,
 * image URL, and file MIME types in [NftProperties].
 */
fun NftMetadata.determineAssetType(): AssetType {
    val animUrl = animationUrl ?: ""
    val imgUrl = image ?: ""
    val fileTypes = properties?.files?.mapNotNull { it.type } ?: emptyList()

    return when {
        isGlbUrl(animUrl) || fileTypes.any { isGlbMimeType(it) } -> Model3d
        isMp4Url(animUrl) || fileTypes.any { isMp4MimeType(it) } -> ImageAndVideo
        isGifUrl(animUrl) || isGifUrl(imgUrl) || fileTypes.any { isGifMimeType(it) } -> AnimatedImage
        else -> Image
    }
}

/**
 * Check if a URL points to a GLB (3D model) file.
 * Handles both direct `.glb` extensions and Arweave-style `?ext=glb` query params.
 */
fun isGlbUrl(url: String): Boolean {
    if (url.isBlank()) return false
    val lower = url.lowercase()
    return lower.contains(".glb") || lower.contains("ext=glb")
}

/**
 * Check if a MIME type indicates a GLB/glTF 3D model file.
 */
fun isGlbMimeType(mimeType: String): Boolean {
    val lower = mimeType.lowercase()
    return lower == "model/gltf-binary" || lower == "model/gltf+json" || lower.contains("gltf")
}

/**
 * Check if a URL points to an animated GIF file.
 * Handles both direct `.gif` extensions and Arweave-style `?ext=gif` query params.
 */
fun isGifUrl(url: String): Boolean {
    if (url.isBlank()) return false
    val lower = url.lowercase()
    return lower.contains(".gif") || lower.contains("ext=gif")
}

/**
 * Check if a MIME type indicates an animated GIF file.
 */
fun isGifMimeType(mimeType: String): Boolean {
    return mimeType.lowercase() == "image/gif"
}

/**
 * Check if a URL points to an MP4 video file.
 * Handles both direct `.mp4` extensions and Arweave-style `?ext=mp4` query params.
 */
fun isMp4Url(url: String): Boolean {
    if (url.isBlank()) return false
    val lower = url.lowercase()
    return lower.contains(".mp4") || lower.contains("ext=mp4")
}

/**
 * Check if a MIME type indicates an MP4 video file.
 */
fun isMp4MimeType(mimeType: String): Boolean {
    return mimeType.lowercase() == "video/mp4"
}

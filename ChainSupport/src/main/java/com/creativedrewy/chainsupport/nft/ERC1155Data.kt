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

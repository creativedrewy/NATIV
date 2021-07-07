package com.creativedrewy.nativ.nft

data class NftMetadata(
    val name: String,
    val symbol: String,
    val description: String,
    val image: String,
    val animationUrl: String,
    val externalUrl: String,
    val properties: NftProperties
)

data class NftProperties(
    val category: String,
    val files: List<String>,
    val creators: List<NftCreator>
)

data class NftCreator(
    val address: String,
    val verified: Boolean,
    val share: Int
)
package com.creativedrewy.solananft.das

import com.google.gson.annotations.SerializedName

data class RpcResultDas<T>(
    val result: T
)

data class DasAssetsList(
    val total: Int,
    val limit: Int,
    val page: Int,
    val items: List<DasAsset>
)

data class DasAsset(
    @SerializedName("interface")
    val interfaceType: String,
    val id: String,
    val content: DasContent?,
    val grouping: List<DasGrouping>?,
    val creators: List<DasCreator>?,
)

data class DasContent(
    @SerializedName("json_uri")
    val jsonUri: String,
    val files: List<DasFile>?,
    val metadata: DasMetadata,
    val links: Map<String, String>?
)

data class DasFile(
    val uri: String?,
    val type: String?
)

data class DasMetadata(
    val attributes: List<DasAttribute>?,
    val description: String?,
    val name: String?,
    val symbol: String?
)

data class DasAttribute(
    @SerializedName("trait_type")
    val traitType: String?,
    val value: String?
)

data class DasGrouping(
    @SerializedName("group_key")
    val groupKey: String,
    @SerializedName("group_value")
    val groupValue: String
)

data class DasCreator(
    val address: String,
)

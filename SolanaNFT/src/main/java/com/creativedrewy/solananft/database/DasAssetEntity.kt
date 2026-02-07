package com.creativedrewy.solananft.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.creativedrewy.solananft.das.DasAsset
import com.creativedrewy.solananft.das.DasAttribute
import com.creativedrewy.solananft.das.DasContent
import com.creativedrewy.solananft.das.DasCreator
import com.creativedrewy.solananft.das.DasFile
import com.creativedrewy.solananft.das.DasGrouping
import com.creativedrewy.solananft.das.DasMetadata

@Entity(tableName = "DasAssetEntity")
data class DasAssetEntity(
    @PrimaryKey val id: String,
    val interfaceType: String,
    @Embedded val content: DasContentEmbedded?,
    val grouping: List<DasGrouping>?,
    val creators: List<DasCreator>?,
    val collectionId: String?,
    val collectionName: String?,
)

data class DasContentEmbedded(
    val jsonUri: String,
    val files: List<DasFile>?,
    @Embedded val metadata: DasMetadataEmbedded,
    val links: Map<String, String>?
)

data class DasMetadataEmbedded(
    val attributes: List<DasAttribute>?,
    val description: String?,
    val name: String?,
    val symbol: String?
)

fun DasAsset.toEntity(): DasAssetEntity = DasAssetEntity(
    id = id,
    interfaceType = interfaceType,
    content = content?.let {
        DasContentEmbedded(
            it.jsonUri,
            it.files,
            DasMetadataEmbedded(
                it.metadata.attributes,
                it.metadata.description,
                it.metadata.name,
                it.metadata.symbol
            ),
            it.links
        )
    },
    grouping = grouping,
    creators = creators,
    collectionId = grouping?.firstOrNull { it.groupKey == "collection" }?.groupValue,
    collectionName = null,
)

fun DasAssetEntity.toDasAsset(): DasAsset = DasAsset(
    interfaceType = interfaceType,
    id = id,
    content = content?.let {
        DasContent(
            it.jsonUri,
            it.files,
            DasMetadata(
                it.metadata.attributes,
                it.metadata.description,
                it.metadata.name,
                it.metadata.symbol
            ),
            it.links
        )
    },
    grouping = grouping,
    creators = creators,
)

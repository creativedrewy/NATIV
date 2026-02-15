package com.creativedrewy.solananft.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoriteNft")
data class FavoriteNft(
    @PrimaryKey val tokenAddress: String,
    val name: String,
    val imageUrl: String,
    val mediaUrl: String,
    val assetType: String,
    val sortOrder: Int = 0
)

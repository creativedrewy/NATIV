package com.creativedrewy.solananft.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [DasAssetEntity::class, FavoriteNft::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NftDatabase : RoomDatabase() {
    abstract fun dasAssetDao(): DasAssetDao
    abstract fun favoriteNftDao(): FavoriteNftDao
}

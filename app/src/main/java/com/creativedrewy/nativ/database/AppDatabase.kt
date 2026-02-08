package com.creativedrewy.nativ.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ ChainAddr::class, FavoriteNft::class ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chainAddrDao(): ChainAddrDao
    abstract fun favoriteNftDao(): FavoriteNftDao
}

package com.creativedrewy.nativ.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ ChainAddr::class ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chainAddrDao(): ChainAddrDao
}

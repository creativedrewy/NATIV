package com.creativedrewy.nativ.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ ChainAddr::class ],
    version = 3
)
abstract class AddressDatabase : RoomDatabase() {
    abstract fun chainAddrDao(): ChainAddrDao
}

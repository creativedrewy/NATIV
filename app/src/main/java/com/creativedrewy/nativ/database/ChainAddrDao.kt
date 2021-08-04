package com.creativedrewy.nativ.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChainAddrDao {

    @Query("SELECT * FROM ChainAddr")
    fun getAllPubKeys(): Flow<List<ChainAddr>>

    @Query("SELECT * FROM ChainAddr")
    fun loadAllPubKeys(): List<ChainAddr>

    @Insert
    fun insertAll(vararg addrs: ChainAddr)

    @Delete
    fun delete(addr: ChainAddr)
}
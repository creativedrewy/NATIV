package com.creativedrewy.nativ.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChainAddrDao {

    @Query("SELECT * FROM ChainAddr")
    fun getAllPubKeys(): List<ChainAddr>

    @Insert
    fun insertAll(vararg addrs: ChainAddr)

    @Delete
    fun delete(addr: ChainAddr)
}
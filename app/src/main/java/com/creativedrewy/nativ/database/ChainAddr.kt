package com.creativedrewy.nativ.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChainAddr(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "pubkey") val pubKey: String?,
    @ColumnInfo(name = "blockchain") val blockchain: String?
)
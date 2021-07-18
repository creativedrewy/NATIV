package com.creativedrewy.nativ.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChainAddr(
    @PrimaryKey val pubKey: String,
    val blockchain: String
)
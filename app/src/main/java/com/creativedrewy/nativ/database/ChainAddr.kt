package com.creativedrewy.nativ.database

import androidx.room.Entity

@Entity(
    primaryKeys = [
        "pubKey",
        "blockchain"
    ]
)
data class ChainAddr(
    val pubKey: String,
    val blockchain: String
)

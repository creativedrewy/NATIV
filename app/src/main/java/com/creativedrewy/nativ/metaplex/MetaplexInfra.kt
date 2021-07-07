package com.creativedrewy.nativ.metaplex

import com.solana.core.PublicKey
import com.solana.vendor.borshj.BorshCodable
import com.solana.vendor.borshj.FieldOrder

object MetaplexContstants {
    const val METADATA_NAME = "metadata"
    const val METADATA_ACCOUNT_PUBKEY = "metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s"
}

data class MetaplexMeta(
    @FieldOrder(0) val key: Byte,
    @FieldOrder(1) val update_authority: PublicKey,
    @FieldOrder(2) val mint: PublicKey,
    @FieldOrder(3) val data: MetaplexData
): BorshCodable

data class MetaplexData(
    @FieldOrder(0) val name: String,
    @FieldOrder(1) val symbol: String,
    @FieldOrder(2) val uri: String
): BorshCodable
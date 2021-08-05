package com.creativedrewy.solananft.rpcapi

import java.util.*

data class Rpc20RequestDto (
    val method: String,
    val params: List<Any>? = null,
    val jsonrpc: String = "2.0",
    val id: String = UUID.randomUUID().toString()
)

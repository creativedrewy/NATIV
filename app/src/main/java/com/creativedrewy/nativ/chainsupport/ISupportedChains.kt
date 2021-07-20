package com.creativedrewy.nativ.chainsupport

interface ISupportedChains {

    val supportedChains
        get() = chainsToNftLoadersMap.keys

    val chainsToNftLoadersMap: Map<SupportedChain, IBlockchainNftLoader>
}
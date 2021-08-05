package com.creativedrewy.nativ.chainsupport

interface ISupportedChains {

    val supportedChains
        get() = chainsToNftLoadersMap.keys

    val chainsToNftLoadersMap: Map<SupportedChain, IBlockchainNftLoader>
}

fun ISupportedChains.findLoaderByTicker(ticker: String): IBlockchainNftLoader? {
    val foundChain = supportedChains.firstOrNull { it.ticker == ticker }

    return chainsToNftLoadersMap[foundChain]
}

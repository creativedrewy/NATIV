package com.creativedrewy.nativ.injection

import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.chainsupport.IBlockchainNftLoader
import com.creativedrewy.nativ.chainsupport.ISupportedChains
import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.ethereumnft.OpenSeaQueryUseCase
import com.creativedrewy.nativ.ethereumnft.OpenSeaRepository
import com.creativedrewy.solananft.metaplex.MetaplexNftUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(
    ViewModelComponent::class
)
@Module()
class BlockchainSupportModule {

    @Provides
    fun proivdesOpenSeaUseCase(repository: OpenSeaRepository): OpenSeaQueryUseCase {
        return OpenSeaQueryUseCase(repository)
    }

    @Provides
    fun providesISupportedChains(
        metaplexLoaderUseCase: MetaplexNftUseCase,
        openSeaLoaderUseCase: OpenSeaQueryUseCase
    ): ISupportedChains {
        return object : ISupportedChains {
            override val chainsToNftLoadersMap: Map<SupportedChain, IBlockchainNftLoader> = mapOf(
                SupportedChain(
                    name = "Solana",
                    ticker = "SOL",
                    iconRes = R.drawable.solana_logo
                ) to metaplexLoaderUseCase,
                SupportedChain(
                    name = "Ethereum",
                    ticker = "ETH",
                    iconRes = R.drawable.eth_diamond_black
                ) to openSeaLoaderUseCase
            )
        }
    }
}

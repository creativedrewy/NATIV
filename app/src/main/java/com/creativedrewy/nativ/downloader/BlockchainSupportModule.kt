package com.creativedrewy.nativ.downloader

import com.creativedrewy.nativ.chainsupport.ISupportedChains
import com.creativedrewy.nativ.metaplex.MetaplexNftUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(
    ViewModelComponent::class
)
@Module
class BlockchainSupportModule {

    @Provides
    fun providesISupportedChains(
        metaplex: MetaplexNftUseCase
    ): ISupportedChains {
        return object : ISupportedChains {
            override val chainsToNftLoaderMap: Map<String, String> = mapOf()
        }
    }

}
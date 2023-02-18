package com.creativedrewy.solananft.injection

import com.creativedrewy.nativ.chainsupport.network.ApiRequestClient
import com.creativedrewy.nativ.chainsupport.nft.NftSpecRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(
    ViewModelComponent::class
)
@Module
class SolanaNftModule {

    @Provides
    fun providesNftSpecRepository(): NftSpecRepository {
        return NftSpecRepository()
    }

    @ViewModelScoped
    @Provides
    fun providesApiRequestClient(): ApiRequestClient {
        return ApiRequestClient()
    }

}
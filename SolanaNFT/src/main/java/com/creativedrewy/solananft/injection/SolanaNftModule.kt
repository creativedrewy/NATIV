package com.creativedrewy.solananft.injection

import com.creativedrewy.nativ.chainsupport.network.ApiRequestClient
import com.creativedrewy.nativ.chainsupport.nft.NftSpecRepository
import com.creativedrewy.solananft.repository.AccountRepository
import com.google.gson.Gson
import com.solana.core.PublicKeyRule
import com.solana.vendor.borshj.Borsh
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
    fun providesBorsh(): Borsh {
        val borsh = Borsh()
        borsh.setRules(listOf(PublicKeyRule()))

        return borsh
    }

    @Provides
    fun providesAccountRepository(): AccountRepository {
        return AccountRepository()
    }

    @Provides
    fun providesNftSpecRepository(): NftSpecRepository {
        return NftSpecRepository()
    }

    @ViewModelScoped
    @Provides
    fun providesApiRequestClient(): ApiRequestClient {
        return ApiRequestClient()
    }

    @ViewModelScoped
    @Provides
    fun proivdesGson(): Gson {
        return Gson()
    }

}
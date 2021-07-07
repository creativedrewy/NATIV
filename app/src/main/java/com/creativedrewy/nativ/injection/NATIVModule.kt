package com.creativedrewy.nativ.injection

import com.creativedrewy.nativ.nft.NftSpecRepository
import com.creativedrewy.solanarepository.accounts.AccountRepository
import com.solana.core.PublicKeyRule
import com.solana.vendor.borshj.Borsh
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(
    ViewModelComponent::class
)
@Module
class NATIVModule {

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

}
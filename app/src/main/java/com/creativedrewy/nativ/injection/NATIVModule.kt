package com.creativedrewy.nativ.injection

import android.content.Context
import androidx.room.Room
import com.creativedrewy.nativ.chainsupport.network.ApiRequestClient
import com.creativedrewy.nativ.chainsupport.nft.NftSpecRepository
import com.creativedrewy.nativ.database.AppDatabase
import com.creativedrewy.nativ.database.ChainAddrDao
import com.creativedrewy.solanarepository.accounts.AccountRepository
import com.google.gson.Gson
import com.solana.core.PublicKeyRule
import com.solana.vendor.borshj.Borsh
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(
    ViewModelComponent::class
)
@Module
class NATIVModule {

    @ViewModelScoped
    @Provides
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "nativ-db"
        ).build()
    }

    @ViewModelScoped
    @Provides
    fun providesChainAddrDao(db: AppDatabase): ChainAddrDao {
        return db.chainAddrDao()
    }

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
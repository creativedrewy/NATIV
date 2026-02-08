package com.creativedrewy.nativ.injection

import android.content.Context
import androidx.room.Room
import com.creativedrewy.nativ.database.AddressDatabase
import com.creativedrewy.nativ.database.ChainAddrDao
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
    fun providesAddressDatabase(@ApplicationContext context: Context): AddressDatabase {
        return Room.databaseBuilder(
            context,
            AddressDatabase::class.java,
            "nativ-db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @ViewModelScoped
    @Provides
    fun providesChainAddrDao(db: AddressDatabase): ChainAddrDao {
        return db.chainAddrDao()
    }
}

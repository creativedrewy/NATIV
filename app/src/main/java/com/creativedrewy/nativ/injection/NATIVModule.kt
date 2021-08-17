package com.creativedrewy.nativ.injection

import android.content.Context
import androidx.room.Room
import com.creativedrewy.nativ.database.AppDatabase
import com.creativedrewy.nativ.database.ChainAddrDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
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
}

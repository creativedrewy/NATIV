package com.creativedrewy.solananft.injection

import android.content.Context
import androidx.room.Room
import com.creativedrewy.solananft.database.DasAssetDao
import com.creativedrewy.solananft.database.FavoriteNftDao
import com.creativedrewy.solananft.database.NftDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideNftDatabase(@ApplicationContext context: Context): NftDatabase =
        Room.databaseBuilder(context, NftDatabase::class.java, "cached_nft")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Singleton
    @Provides
    fun provideDasAssetDao(db: NftDatabase): DasAssetDao = db.dasAssetDao()

    @Singleton
    @Provides
    fun provideFavoriteNftDao(db: NftDatabase): FavoriteNftDao = db.favoriteNftDao()
}
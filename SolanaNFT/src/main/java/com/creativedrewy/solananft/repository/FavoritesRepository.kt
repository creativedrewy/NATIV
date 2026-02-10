package com.creativedrewy.solananft.repository

import com.creativedrewy.solananft.database.FavoriteNft
import com.creativedrewy.solananft.database.FavoriteNftDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val favoriteNftDao: FavoriteNftDao
) {

    fun observeAllFavorites(): Flow<List<FavoriteNft>> = favoriteNftDao.observeAll()

    suspend fun addFavorite(nft: FavoriteNft) = withContext(Dispatchers.IO) {
        favoriteNftDao.insert(nft)
    }

    suspend fun removeFavorite(tokenAddress: String) = withContext(Dispatchers.IO) {
        favoriteNftDao.delete(tokenAddress)
    }

    suspend fun isFavorited(tokenAddress: String): Boolean = withContext(Dispatchers.IO) {
        favoriteNftDao.getFavorite(tokenAddress) != null
    }

    suspend fun getAllFavorites(): List<FavoriteNft> = withContext(Dispatchers.IO) {
        favoriteNftDao.getAll()
    }
}

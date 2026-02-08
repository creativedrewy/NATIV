package com.creativedrewy.solananft.repository

import android.content.Context
import com.creativedrewy.solananft.database.FavoriteNft
import com.creativedrewy.solananft.database.FavoriteNftDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val favoriteNftDao: FavoriteNftDao,
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val CACHE_DIR = "wallpaper_cache"
    }

    suspend fun addFavorite(nft: FavoriteNft) = withContext(Dispatchers.IO) {
        favoriteNftDao.insert(nft)
    }

    suspend fun removeFavorite(tokenAddress: String) = withContext(Dispatchers.IO) {
        favoriteNftDao.delete(tokenAddress)
        deleteCachedFile(tokenAddress)
    }

    suspend fun isFavorited(tokenAddress: String): Boolean = withContext(Dispatchers.IO) {
        favoriteNftDao.getFavorite(tokenAddress) != null
    }

    suspend fun getAllFavorites(): List<FavoriteNft> = withContext(Dispatchers.IO) {
        favoriteNftDao.getAll()
    }

    suspend fun cacheMediaFile(tokenAddress: String, bytes: ByteArray) =
        withContext(Dispatchers.IO) {
            val dir = File(context.filesDir, CACHE_DIR)
            dir.mkdirs()
            File(dir, tokenAddress).writeBytes(bytes)
        }

    private fun deleteCachedFile(tokenAddress: String) {
        val file = File(context.filesDir, "$CACHE_DIR/$tokenAddress")
        if (file.exists()) file.delete()
    }
}
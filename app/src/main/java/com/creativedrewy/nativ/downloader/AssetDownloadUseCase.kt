package com.creativedrewy.nativ.downloader

import com.creativedrewy.solanarepository.ApiRequestClient
import com.creativedrewy.solanarepository.Error
import com.creativedrewy.solanarepository.Success
import okhttp3.Request
import javax.inject.Inject

class AssetDownloadUseCase @Inject constructor(
    val apiRequestClient: ApiRequestClient
) {

    suspend fun downloadAsset(uri: String): ByteArray {
        val request = Request.Builder()
            .url(uri)
            .get()
            .build()

        val result = apiRequestClient.apiRequest(request)
        return when (result) {
            is Success -> {
                val bytes = result.response.body?.bytes() ?: byteArrayOf()
                bytes
            }
            is Error -> {
                byteArrayOf()
            }
        }
    }

}
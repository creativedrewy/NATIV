package com.creativedrewy.nativ.nft

import com.creativedrewy.solanarepository.ApiRequestClient
import com.creativedrewy.solanarepository.Error
import com.creativedrewy.solanarepository.Success
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class NftSpecRepository(
    private val apiRequestClient: ApiRequestClient = ApiRequestClient(OkHttpClient()),
    private val gson: Gson = Gson()
) {

    suspend fun getNftDetails(uri: String): NftMetadata? {
        val request = Request.Builder()
            .url(uri)
            .get()
            .build()

        val result = apiRequestClient.apiRequest(request)

        return when (result) {
            is Success -> {
                val resString = result.response.body?.string()

                gson.fromJson(resString, NftMetadata::class.java)
            }
            is Error -> {
                null
            }
        }
    }

}
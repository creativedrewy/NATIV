package com.creativedrewy.nativ.chainsupport.nft

import com.creativedrewy.solanarepository.ApiRequestClient
import com.google.gson.Gson
import com.sun.net.httpserver.Authenticator
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
            is Authenticator.Success -> {
                val resString = result.response.body?.string()

                gson.fromJson(resString, NftMetadata::class.java)
            }
            is Error -> {
                null
            }
        }
    }

}
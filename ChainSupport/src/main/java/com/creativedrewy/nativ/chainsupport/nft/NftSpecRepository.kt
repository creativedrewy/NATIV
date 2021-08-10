package com.creativedrewy.nativ.chainsupport.nft

import com.creativedrewy.nativ.chainsupport.NftPropertiesDeserializer
import com.creativedrewy.nativ.chainsupport.network.ApiRequestClient
import com.creativedrewy.nativ.chainsupport.network.Error
import com.creativedrewy.nativ.chainsupport.network.Success
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request

class NftSpecRepository(
    private val apiRequestClient: ApiRequestClient = ApiRequestClient(OkHttpClient()),
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(NftProperties::class.java, NftPropertiesDeserializer())
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()
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

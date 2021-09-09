package com.creativedrewy.nativ.harmonyonenft

import android.util.Log
import com.creativedrewy.nativ.chainsupport.NftPropertiesDeserializer
import com.creativedrewy.nativ.chainsupport.network.ApiRequestClient
import com.creativedrewy.nativ.chainsupport.network.Error
import com.creativedrewy.nativ.chainsupport.network.Success
import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import javax.inject.Inject

class HarmonyNftRepository @Inject constructor(
    private val apiRequestClient: ApiRequestClient,
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(NftProperties::class.java, NftPropertiesDeserializer())
        .serializeNulls()
        .create()
) {

    companion object {
        const val HARMONY_BASE = "https://explorer-v2-api.hmny.io/v0/"

        const val ERC721_ADDR = "erc721/address/"
        const val ERC1155_INDEX = "erc1155/address/"
        const val ERC1155_TOKEN = "erc1155/token/"

        const val BALANCES = "/balances"
        const val ASSETS = "/assets"
    }

    suspend fun getErc721Nfts(addr: String): List<Erc721ResultDto> {
        val url = "$HARMONY_BASE$ERC721_ADDR$addr$BALANCES"

        return getRemoteList(url, object : TypeToken<List<Erc721ResultDto>>(){})
    }

    //https://explorer-v2-api.hmny.io/v0/erc1155/address/0x93e5c9ca043f00a50f28d3f5a6638172813e1a71/balances
    //https://explorer-v2-api.hmny.io/v0/erc1155/token/0x29ff1684bf3d3dd53be4a507b83648897f9d244e/assets

    suspend fun getErc155Nfts() {

    }

    private suspend fun <T> getRemoteList(url: String, token: TypeToken<List<T>>): List<T> {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return withContext(Dispatchers.IO) {
            var resultString = ""

            when (val result = apiRequestClient.apiRequest(request)) {
                is Success -> {
                    try {
                        val typeToken = token.type

                        resultString = result.response.body?.string() ?: ""
                        val dto = gson.fromJson<List<T>>(resultString, typeToken)

                        dto
                    } catch (e: Exception) {
                        Log.e("Harmony", "Error parsing Harmony ERC721 result: $resultString")

                        listOf()
                    }
                }
                is Error -> {
                    listOf()
                }
            }
        }
    }

}
package com.creativedrewy.nativ.harmonyonenft

import android.util.Log
import com.creativedrewy.nativ.chainsupport.network.ApiRequestClient
import com.creativedrewy.nativ.chainsupport.network.Error
import com.creativedrewy.nativ.chainsupport.network.Success
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import javax.inject.Inject

class HarmonyNftRepository @Inject constructor(
    private val apiRequestClient: ApiRequestClient,
    private val gson: Gson
) {

    companion object {
        const val HARMONY_BASE = "https://explorer-v2-api.hmny.io/v0/"
        const val ERC721_ADDR = "erc721/address/"
    }

    //https://explorer-v2-api.hmny.io/v0/erc721/address/0x93e5c9ca043f00a50f28d3f5a6638172813e1a71/balances

    suspend fun getErc721Nfts(addr: String): List<Erc721ResultDto> {
        val url = "$HARMONY_BASE$ERC721_ADDR$addr/balances"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return withContext(Dispatchers.IO) {
            var resultString = ""

            when (val result = apiRequestClient.apiRequest(request)) {
                is Success -> {
                    try {
                        val typeToken = object : TypeToken<List<Erc721ResultDto>>() {}.type

                        resultString = result.response.body?.string() ?: ""
                        val dto = gson.fromJson<List<Erc721ResultDto>>(resultString, typeToken)

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
package com.creativedrewy.nativ.opensea

import android.util.Log
import com.creativedrewy.nativ.chainsupport.network.ApiRequestClient
import com.creativedrewy.nativ.chainsupport.network.Error
import com.creativedrewy.nativ.chainsupport.network.Success
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import javax.inject.Inject

class OpenSeaRepository @Inject constructor(
    private val apiRequestClient: ApiRequestClient,
    private val gson: Gson
) {
    companion object {
        const val OPENSEA_BASE = "https://api.opensea.io/api/v1/assets?owner="
    }

    suspend fun getNftsForAddress(addr: String): OpenSeaResultsDto {
        val request = Request.Builder()
            .url(OPENSEA_BASE + addr)
            .get()
            .build()

        return withContext(Dispatchers.IO) {
            var resultString = ""

            when (val result = apiRequestClient.apiRequest(request)) {
                is Success -> {
                    try {
                        resultString = result.response.body?.string() ?: ""
                        val dto = gson.fromJson(resultString, OpenSeaResultsDto::class.java)

                        dto
                    } catch (e: Exception) {
                        Log.e("Ethereum", "Error parsing OpenSea result: $resultString")

                        OpenSeaResultsDto(listOf())
                    }
                }
                is Error -> {
                    OpenSeaResultsDto(listOf())
                }
            }
        }
    }

}
package com.creativedrewy.nativ.harmonyonenft

import com.creativedrewy.nativ.chainsupport.network.ApiRequestClient
import com.google.gson.Gson
import javax.inject.Inject

class HarmonyNftRepository @Inject constructor(
    private val apiRequestClient: ApiRequestClient,
    private val gson: Gson
) {

    companion object {
        const val HARMONY_BASE = "https://explorer-v2-api.hmny.io/v0/"
    }

//    suspend fun getNftsForAddress(addr: String): OpenSeaResultsDto {
//        val request = Request.Builder()
//            .url(HARMONY_BASE + addr.lowercase(Locale.getDefault()))
//            .get()
//            .build()
//
//        return withContext(Dispatchers.IO) {
//            var resultString = ""
//
//            when (val result = apiRequestClient.apiRequest(request)) {
//                is Success -> {
//                    try {
//                        resultString = result.response.body?.string() ?: ""
//                        val dto = gson.fromJson(resultString, OpenSeaResultsDto::class.java)
//
//                        dto
//                    } catch (e: Exception) {
//                        Log.e("Harmony", "Error parsing OpenSea result: $resultString")
//
//                        OpenSeaResultsDto(listOf())
//                    }
//                }
//                is Error -> {
//                    OpenSeaResultsDto(listOf())
//                }
//            }
//        }
//    }

}
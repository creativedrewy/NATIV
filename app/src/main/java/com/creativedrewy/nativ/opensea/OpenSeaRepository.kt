package com.creativedrewy.nativ.opensea

import com.creativedrewy.solanarepository.ApiRequestClient
import com.creativedrewy.solanarepository.Error
import com.creativedrewy.solanarepository.Success
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
            when (val result = apiRequestClient.apiRequest(request)) {
                is Success -> {
                    val resultString = result.response.body?.string()
                    val dto = gson.fromJson(resultString, OpenSeaResultsDto::class.java)

                    dto
                }
                is Error -> {
                    OpenSeaResultsDto(listOf())
                }
            }
        }
    }

}
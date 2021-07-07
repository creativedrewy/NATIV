package com.creativedrewy.solanarepository.rpcapi

import com.creativedrewy.solanarepository.ApiRequestClient
import com.creativedrewy.solanarepository.ResponseStatus
import com.google.gson.Gson
import com.solana.networking.RPCEndpoint
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class RpcRequestClient(
    private val rpcEndpoint: RPCEndpoint,
    private val gson: Gson = Gson(),
    private val apiRequestClient: ApiRequestClient = ApiRequestClient(OkHttpClient())
) {

    companion object {
        val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
    }

    suspend fun makeRequest(requestDto: Rpc20RequestDto): ResponseStatus {
        val request = Request.Builder()
            .url(rpcEndpoint.url)
            .post(gson.toJson(requestDto).toRequestBody(JSON))
            .build()

        return apiRequestClient.apiRequest(request)
    }

}
package com.creativedrewy.solanarepository

import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

sealed class ResponseStatus

class Error(
    val exception: Exception
): ResponseStatus()

class Success(
    val response: Response
): ResponseStatus()

class ApiRequestClient(
    private val okHttpClient: OkHttpClient = OkHttpClient()
) {

    suspend fun apiRequest(request: Request): ResponseStatus {
        return suspendCoroutine { cont ->
            val call = okHttpClient.newCall(request)

            call.enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cont.resume(Error(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    cont.resume(Success(response))
                }
            })
        }
    }

}
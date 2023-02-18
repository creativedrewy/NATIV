package com.creativedrewy.solananft.metaplex

import com.metaplex.lib.drivers.network.HttpNetworkDriver
import com.metaplex.lib.drivers.network.HttpPostRequest
import com.metaplex.lib.drivers.network.HttpRequest
import com.metaplex.lib.drivers.rpc.JsonRpcDriver
import com.metaplex.lib.drivers.rpc.RpcRequest
import com.metaplex.lib.drivers.rpc.RpcResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

/**
 * TODO: This will be removed w/ an update to metaplex sdk
 */
class NewJdkHttpDriver : HttpNetworkDriver {
    override suspend fun makeHttpRequest(request: HttpRequest): String =
        suspendCancellableCoroutine { continuation ->

            with(URL(request.url).openConnection() as HttpURLConnection) {
                requestMethod = request.method
                request.properties.forEach { (key, value) ->
                    setRequestProperty(key, value)
                }

                continuation.invokeOnCancellation { disconnect() }

                request.body?.run {
                    doOutput = true
                    outputStream.write(toByteArray(Charsets.UTF_8))
                    outputStream.flush()
                    outputStream.close()
                }

                val responseString = when (responseCode) {
                    HttpURLConnection.HTTP_OK -> inputStream.bufferedReader().use { it.readText() }
                    else -> errorStream.bufferedReader().use { it.readText() }
                }

                continuation.resumeWith(Result.success(responseString))
            }
        }
}

/**
 * TODO: This will be removed w/ an update to metaplex sdk
 */
class NewJdkRpcDriver(val url: String) : JsonRpcDriver {

    constructor(url: URL) : this(url.toString())

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    override suspend fun <R> makeRequest(request: RpcRequest, resultSerializer: KSerializer<R>): RpcResponse<R> =
        NewJdkHttpDriver().makeHttpRequest(
            HttpPostRequest(
                url = url,
                properties = mapOf("Content-Type" to "application/json; charset=utf-8"),
                body = json.encodeToString(RpcRequest.serializer(), request)
            )
        ).run {
            json.decodeFromString(RpcResponse.serializer(resultSerializer), this)
        }
}
package com.creativedrewy.solananft.accounts

import android.util.Log
import com.creativedrewy.nativ.chainsupport.network.Error
import com.creativedrewy.nativ.chainsupport.network.Success
import com.creativedrewy.solananft.BuildConfig
import com.creativedrewy.solananft.das.DasAsset
import com.creativedrewy.solananft.das.DasAssetsList
import com.creativedrewy.solananft.das.RpcResultDas
import com.creativedrewy.solananft.rpcapi.Rpc20ObjectParamsDto
import com.creativedrewy.solananft.rpcapi.RpcRequestClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.solana.core.PublicKey
import com.solana.networking.Network
import com.solana.networking.RPCEndpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class AccountRepository(
    private val rpcRequestClient: RpcRequestClient = RpcRequestClient(RPCEndpoint.custom(
        URL("https://mainnet.helius-rpc.com/?api-key=${BuildConfig.API_KEY}"),
        URL("https://mainnet.helius-rpc.com/?api-key=${BuildConfig.API_KEY}"),
        Network.mainnetBeta
    )),
    private val gson: Gson = Gson(),
) {

    suspend fun getAssetsByOwner(owner: PublicKey): List<DasAsset> = withContext(Dispatchers.IO) {
        val allItems = mutableListOf<DasAsset>()

        var page = 1
        val limit = 50

        while (true) {
            val params = mapOf<String, Any>(
                "ownerAddress" to owner.toString(),
                "page" to page,
                "limit" to limit
            )

            val rpcRequest = Rpc20ObjectParamsDto("getAssetsByOwner", params)

            when (val result = rpcRequestClient.makeRequest(rpcRequest)) {
                is Success -> {
                    val resultString = result.response.body?.string() ?: continue

                    try {
                        val typeToken = object : TypeToken<RpcResultDas<DasAssetsList>>() {}.type
                        val dto =
                            gson.fromJson<RpcResultDas<DasAssetsList>>(resultString, typeToken)

                        val assets = dto.result.items
                        allItems.addAll(assets)

                        if (assets.isEmpty() || allItems.size >= dto.result.total) {
                            break
                        }

                        page++
                    } catch (e: Exception) {
                        Log.e("Solana", "Error parsing DAS response: $resultString", e)
                        break
                    }
                }

                is Error -> {
                    Log.e(
                        "Solana",
                        "Error fetching DAS assets: ${result.exception?.message}",
                        result.exception
                    )
                    break
                }
            }
        }

        allItems
    }

    /**
     * Fetch a single asset by its ID using the DAS getAsset method.
     * Used primarily to resolve collection metadata (name, image, etc.)
     */
    suspend fun getAsset(assetId: String): DasAsset? = withContext(Dispatchers.IO) {
        val params = mapOf<String, Any>(
            "id" to assetId
        )

        val rpcRequest = Rpc20ObjectParamsDto("getAsset", params)

        when (val result = rpcRequestClient.makeRequest(rpcRequest)) {
            is Success -> {
                val resultString = result.response.body?.string() ?: return@withContext null

                try {
                    val typeToken = object : TypeToken<RpcResultDas<DasAsset>>() {}.type
                    val dto = gson.fromJson<RpcResultDas<DasAsset>>(resultString, typeToken)
                    dto.result
                } catch (e: Exception) {
                    Log.e("Solana", "Error parsing getAsset response for $assetId", e)
                    null
                }
            }

            is Error -> {
                Log.e("Solana", "Error fetching asset $assetId: ${result.exception?.message}", result.exception)
                null
            }
        }
    }
}

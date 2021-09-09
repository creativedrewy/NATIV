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
        const val IPFS_ASSET_BASE = "https://ipfs.io/ipfs/"
        const val HARMONY_BASE = "https://explorer-v2-api.hmny.io/v0/"

        const val ERC721_ADDR = "erc721/address/"
        const val ERC1155_INDEX = "erc1155/address/"
        const val ERC1155_TOKEN = "erc1155/token/"

        const val BALANCES = "/balances"
        const val ASSETS = "/assets"
    }

    suspend fun getErc721Nfts(addr: String): List<HarmonyNftResultDto> {
        val url = "$HARMONY_BASE$ERC721_ADDR$addr$BALANCES"

        return getRemoteList(url)
    }

    suspend fun getErc155Nfts(addr: String): List<HarmonyNftResultDto> {
        val balancesUrl = "$HARMONY_BASE$ERC1155_INDEX$addr$BALANCES"

        val erc1155List = getRemoteList(balancesUrl)

        return erc1155List.flatMap {
            val tokenAddr = it.tokenAddress
            val tokenId = it.tokenID

            val assetsUrl = "$HARMONY_BASE$ERC1155_TOKEN$tokenAddr$ASSETS"
            val allTokenAssets = getRemoteList(assetsUrl)

            allTokenAssets
                .filter { it.tokenID == tokenId }
                .map { dto ->
                    dto.copy(
                        meta = dto.meta?.copy(
                            image = "$IPFS_ASSET_BASE${dto.meta.image}"
                        )
                    )
                }
        }
    }

    private suspend fun getRemoteList(url: String): List<HarmonyNftResultDto> {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return withContext(Dispatchers.IO) {
            var resultString = ""

            when (val result = apiRequestClient.apiRequest(request)) {
                is Success -> {
                    try {
                        val typeToken = object : TypeToken<List<HarmonyNftResultDto>>() {}.type

                        resultString = result.response.body?.string() ?: ""
                        val dto = gson.fromJson<List<HarmonyNftResultDto>>(resultString, typeToken)

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
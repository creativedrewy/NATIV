package com.creativedrewy.nativ.harmonyonenft

import android.util.Log
import com.creativedrewy.nativ.chainsupport.NftPropertiesDeserializer
import com.creativedrewy.nativ.chainsupport.network.ApiRequestClient
import com.creativedrewy.nativ.chainsupport.network.Error
import com.creativedrewy.nativ.chainsupport.network.Success
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.lang.reflect.Type
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

        val erc721Dtos = getRemoteList<List<HarmonyNftResultDto>>(url, object : TypeToken<List<HarmonyNftResultDto>>() {}.type) ?: listOf()

        return erc721Dtos.map { dto ->
            if (dto.meta?.name.isNullOrEmpty() && dto.meta?.image.isNullOrEmpty()) {
                HarmonyNftResultDto(
                    meta = getRemoteList<NftMetadata>(dto.tokenURI ?: "", object : TypeToken<NftMetadata>() {}.type)
                )
            } else {
                dto
            }
        }
    }

//    suspend fun getErc155Nfts(addr: String): List<HarmonyNftResultDto> {
//        val balancesUrl = "$HARMONY_BASE$ERC1155_INDEX$addr$BALANCES"
//
//        val erc1155List = getRemoteList(balancesUrl)
//
//        return erc1155List.flatMap {
//            val tokenAddr = it.tokenAddress
//            val tokenId = it.tokenID
//
//            val assetsUrl = "$HARMONY_BASE$ERC1155_TOKEN$tokenAddr$ASSETS"
//            val allTokenAssets = getRemoteList(assetsUrl, object : TypeToken<List<HarmonyNftResultDto>>() {}.type)
//
//            allTokenAssets
//                .filter { it.tokenID == tokenId }
//                .map { dto ->
//                    dto.copy(
//                        meta = dto.meta?.copy(
//                            image = "$IPFS_ASSET_BASE${dto.meta.image}"
//                        )
//                    )
//                }
//        }
//    }

    private suspend fun <T> getRemoteList(url: String, type: Type): T? {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return withContext(Dispatchers.IO) {
            var resultString = ""

            when (val result = apiRequestClient.apiRequest(request)) {
                is Success -> {
                    try {
                        resultString = result.response.body?.string() ?: ""
                        val dto = gson.fromJson<T>(resultString, type)

                        dto
                    } catch (e: Exception) {
                        Log.e("Harmony", "Error parsing Harmony ERC721 result: $resultString")
                        null
                    }
                }
                is Error -> {
                    null
                }
            }
        }
    }

}
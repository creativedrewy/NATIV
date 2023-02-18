package com.creativedrewy.nativ.chainsupport.nft

import com.creativedrewy.nativ.chainsupport.network.ApiRequestClient
import com.creativedrewy.nativ.chainsupport.network.Error
import com.creativedrewy.nativ.chainsupport.network.Success
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class NftSpecRepository(
    private val apiRequestClient: ApiRequestClient = ApiRequestClient(OkHttpClient()),
) {

    @OptIn(
        ExperimentalSerializationApi::class
    )
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    suspend fun getNftDetails(uri: String): NativJsonMetadata? {
        val request = Request.Builder()
            .url(uri)
            .get()
            .build()

        val result = apiRequestClient.apiRequest(request)

        return when (result) {
            is Success -> {
                val resString = result.response.body?.string()

                json.decodeFromString<NativJsonMetadata>(resString ?: "")
            }
            is Error -> {
                null
            }
        }
    }
}

/**
 * TODO: This will likely become replaced with a future version of the metaplex SDK
 */
@Serializable
data class NativJsonMetadata(
    val name: String,
    val description: String,
    val image: String,
    @SerialName("external_url") val externalUrl: String?,
    val attributes: List<Attribute>? = null,
    val properties: Properties? = null
) {
    @Serializable
    data class Attribute(
        @SerialName("trait_type") val traitType: String,
        val value: String
    )

    @Serializable
    data class Properties(
        val files: List<File>?,
        val category: String?
    ) {
        @Serializable
        data class File(
            val uri: String,
            val type: String,
            val cdn: Boolean? = null
        )
    }
}

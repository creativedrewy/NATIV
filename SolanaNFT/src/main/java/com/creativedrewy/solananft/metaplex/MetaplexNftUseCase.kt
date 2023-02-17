package com.creativedrewy.solananft.metaplex

import com.creativedrewy.nativ.chainsupport.IBlockchainNftLoader
import com.creativedrewy.nativ.chainsupport.LoaderNftResult
import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.chainsupport.nft.NftMetaStatus
import com.creativedrewy.nativ.chainsupport.nft.NftSpecRepository
import com.creativedrewy.nativ.chainsupport.nft.Pending
import com.creativedrewy.solananft.accounts.AccountRepository
import com.metaplex.lib.Metaplex
import com.metaplex.lib.drivers.indenty.ReadOnlyIdentityDriver
import com.metaplex.lib.drivers.network.HttpNetworkDriver
import com.metaplex.lib.drivers.network.HttpPostRequest
import com.metaplex.lib.drivers.network.HttpRequest
import com.metaplex.lib.drivers.rpc.JsonRpcDriver
import com.metaplex.lib.drivers.rpc.RpcRequest
import com.metaplex.lib.drivers.rpc.RpcResponse
import com.metaplex.lib.drivers.solana.SolanaConnectionDriver
import com.metaplex.lib.drivers.storage.OkHttpSharedStorageDriver
import com.solana.core.PublicKey
import com.solana.networking.RPCEndpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

/**
 * TODO: This will be removed w/ an update to metaplex sdk
 */
class NewJdkHttpDriver : HttpNetworkDriver {
    override suspend fun makeHttpRequest(request: HttpRequest): String =
        suspendCancellableCoroutine { continuation ->

            with(URL(request.url).openConnection() as HttpURLConnection) {
                // config
                requestMethod = request.method
                request.properties.forEach { (key, value) ->
                    setRequestProperty(key, value)
                }

                // cancellation
                continuation.invokeOnCancellation { disconnect() }

                // send request body
                request.body?.run {
                    doOutput = true
                    outputStream.write(toByteArray(Charsets.UTF_8))
                    outputStream.flush()
                    outputStream.close()
                }

                // read response
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

class MetaplexNftUseCase @Inject constructor(
    private val accountsRepository: AccountRepository,
    private val nftSpecRepository: NftSpecRepository
) : IBlockchainNftLoader {

    /**
     * Load and emit the full set of *possible* NFTs, then emit each NFT's metadata as it is loaded
     */
    override suspend fun loadNftsThenMetaForAddress(chain: SupportedChain, address: String): Flow<LoaderNftResult> = channelFlow {
        withContext(Dispatchers.IO) {
            val ownerPublicKey = PublicKey(address)
            val solanaConnection = SolanaConnectionDriver(NewJdkRpcDriver(RPCEndpoint.devnetSolana.url))
            val solanaIdentityDriver = ReadOnlyIdentityDriver(ownerPublicKey, solanaConnection)
            val storageDriver = OkHttpSharedStorageDriver()
            val metaplex = Metaplex(solanaConnection, solanaIdentityDriver, storageDriver)

            val result = metaplex.nft.findAllByOwner(ownerPublicKey)
            val nfts = result.getOrThrow().filterNotNull()

            val statusMap = mutableMapOf<String, NftMetaStatus>()
            nfts.map { it.uri }.forEach { uri ->
                statusMap[uri] = Pending
            }

            //First emit the uris with "pending" entries for loading status
            send(LoaderNftResult(chain, statusMap))
        }

//        nfts.filterNotNull().forEach {
//            val meta = it.metadata(storageDriver).getOrThrow()
//        }


//        metaUris.forEach { uri ->
//            try {
//                val details = withContext(Dispatchers.IO) {
//                    nftSpecRepository.getNftDetails(uri)
//                }
//
//                details?.let { item ->
//                    statusMap[uri] = MetaLoaded(item)
//                }
//
//                //Emit each loaded & parsed metadata entry as they come in
//                emit(LoaderNftResult(chain, statusMap))
//            } catch (e: Exception) {
//                Log.e("SOL", "Attached data is not Metaplex Meta format", e)
//                statusMap[uri] = Invalid
//            }
//        }
    }
}

package com.creativedrewy.solananft.metaplex

import com.creativedrewy.nativ.chainsupport.IBlockchainNftLoader
import com.creativedrewy.nativ.chainsupport.LoaderNftResult
import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.chainsupport.nft.*
import com.creativedrewy.solananft.BuildConfig
import com.metaplex.lib.Metaplex
import com.metaplex.lib.drivers.indenty.ReadOnlyIdentityDriver
import com.metaplex.lib.drivers.solana.SolanaConnectionDriver
import com.metaplex.lib.drivers.storage.OkHttpSharedStorageDriver
import com.solana.core.PublicKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

class MetaplexNftUseCase @Inject constructor(
    private val nftSpecRepository: NftSpecRepository
) : IBlockchainNftLoader {

    /**
     * Load and emit the full set of *possible* NFTs, then emit each NFT's metadata as it is loaded
     */
    override suspend fun loadNftsThenMetaForAddress(chain: SupportedChain, address: String): Flow<LoaderNftResult> = channelFlow {
        val ownerPublicKey = PublicKey(address)
        val solanaConnection = SolanaConnectionDriver(NewJdkRpcDriver(URL(BuildConfig.RPC_BASE_URL + BuildConfig.RPC_API_KEY)))
        val solanaIdentityDriver = ReadOnlyIdentityDriver(ownerPublicKey, solanaConnection)
        val storageDriver = OkHttpSharedStorageDriver()
        val metaplex = Metaplex(solanaConnection, solanaIdentityDriver, storageDriver)

        val nfts = withContext(Dispatchers.IO) {
            val result = metaplex.nft.findAllByOwner(ownerPublicKey)
            result.getOrThrow().filterNotNull()
        }

        val statusMap = mutableMapOf<String, NftMetaStatus>()
        nfts.map { it.uri }.forEach { uri ->
            statusMap[uri] = Pending
        }

        //First emit the uris with "pending" entries for loading status
        send(LoaderNftResult(chain, statusMap))

        nfts.sortedBy { it.name }
            .forEach { nft ->
                val details = withContext(Dispatchers.IO) {
                    nftSpecRepository.getNftDetails(nft.uri)
                }

                val mappedDetails = mapToIntermediaryMeta(details!!)
                statusMap[nft.uri] = MetaLoaded(mappedDetails)

                send(LoaderNftResult(chain, statusMap))
            }
    }

    private fun mapToIntermediaryMeta(metadata: NativJsonMetadata): NftMetadata {
        return NftMetadata(
            name = metadata.name,
            description = metadata.description,
            image = metadata.image,
            animationUrl = "",
            externalUrl = metadata.externalUrl.orEmpty(),
            attributes = metadata.attributes?.map { attrib ->
                NftAttributes(
                    traitType = attrib.traitType,
                    value = attrib.value
                )
            } ?: listOf(),
            properties = NftProperties(
                category = metadata.properties?.category.orEmpty(),
                files = metadata.properties?.files?.map {
                    FileDetails(
                        uri = it.uri,
                        type = it.type
                    )
                } ?: listOf()
            )
        )
    }
}

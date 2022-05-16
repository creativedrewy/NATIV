package com.creativedrewy.solananft.metaplex

import android.util.Log
import com.creativedrewy.nativ.chainsupport.IBlockchainNftLoader
import com.creativedrewy.nativ.chainsupport.nft.*
import com.creativedrewy.solananft.accounts.AccountRepository
import com.solana.core.PublicKey
import com.solana.vendor.borshj.Borsh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.bitcoinj.core.Base58
import java.util.*
import javax.inject.Inject

class MetaplexNftUseCase @Inject constructor(
    private val accountsRepository: AccountRepository,
    private val nftSpecRepository: NftSpecRepository,
    private val borsh: Borsh
) : IBlockchainNftLoader {

    override suspend fun loadNftsForAddress(address: String): List<NftMetadata> {
        val metaplexNfts = mutableListOf<NftMetadata>()
        try {
            val accountKey = PublicKey(address)

            val ownerAccounts = accountsRepository.getTokenAccountsByOwner(accountKey)
            ownerAccounts.filter { acct ->
                acct.account.data.parsed.info.tokenAmount.amount == 1.0 &&
                    acct.account.data.parsed.info.tokenAmount.decimals == 0.0
            }.forEach {
                val mintAddress = it.account.data.parsed.info.mint

                val pdaSeeds = listOf(
                    MetaplexContstants.METADATA_NAME.toByteArray(),
                    Base58.decode(MetaplexContstants.METADATA_ACCOUNT_PUBKEY),
                    Base58.decode(mintAddress)
                )

                val pdaAddr = PublicKey.findProgramAddress(
                    pdaSeeds,
                    PublicKey(MetaplexContstants.METADATA_ACCOUNT_PUBKEY)
                )

                val accountInfo = accountsRepository.getAccountInfo(pdaAddr.address)
                try {
                    val borshData = Base64.getDecoder().decode(accountInfo.data[0])
                    val metaplexData: MetaplexMeta = borsh.deserialize(borshData, MetaplexMeta::class.java)

                    // Sometimes the borsh-deserialized data has NUL chars on the end, so we need to sanitize
                    val sanitizedUri = metaplexData.data.uri.replace("\u0000", "")

                    val details = withContext(Dispatchers.IO) {
                        //nftSpecRepository.getNftDetails(sanitizedUri)
                        NftMetadata(null, null, null, null, null, null, null, null)
                    }
                    details?.let { item -> metaplexNfts.add(item) }
                } catch (e: Exception) {
                    Log.e("SOL", "Attached data is not Metaplex Meta format", e)
                }
            }
        } catch (e: Exception) {
            Log.e("SOL", "Error attempting to load nfts for address $address", e)
        }

        return metaplexNfts
    }

    override suspend fun loadNftsThenMetaForAddress(address: String): Flow<Map<String, NftMetaStatus>> = flow {
        val metaUris = loadNftMetadataUris(address)

        val statusMap = mutableMapOf<String, NftMetaStatus>()
        metaUris.forEach { uri ->
            statusMap[uri] = Pending
        }

        emit(statusMap)

        metaUris.forEach { uri ->
            try {
                val details = withContext(Dispatchers.IO) {
                    nftSpecRepository.getNftDetails(uri)
                }

                details?.let { item ->
                    statusMap[uri] = MetaLoaded(item)
                }

                emit(statusMap)
            } catch (e: Exception) {
                Log.e("SOL", "Attached data is not Metaplex Meta format", e)
                statusMap.remove(uri)
            }
        }
    }

    /**
     *
     */
    private suspend fun loadNftMetadataUris(address: String): List<String> {
        val nftUris = mutableListOf<String>()

        try {
            val accountKey = PublicKey(address)

            val ownerAccounts = accountsRepository.getTokenAccountsByOwner(accountKey)
            ownerAccounts.filter { acct ->
                acct.account.data.parsed.info.tokenAmount.amount == 1.0 &&
                        acct.account.data.parsed.info.tokenAmount.decimals == 0.0
            }.map {
                val mintAddress = it.account.data.parsed.info.mint

                val pdaSeeds = listOf(
                    MetaplexContstants.METADATA_NAME.toByteArray(),
                    Base58.decode(MetaplexContstants.METADATA_ACCOUNT_PUBKEY),
                    Base58.decode(mintAddress)
                )

                val pdaAddr = PublicKey.findProgramAddress(
                    pdaSeeds,
                    PublicKey(MetaplexContstants.METADATA_ACCOUNT_PUBKEY)
                )

                val accountInfo = accountsRepository.getAccountInfo(pdaAddr.address)
                try {
                    val borshData = Base64.getDecoder().decode(accountInfo.data[0])
                    val metaplexData: MetaplexMeta = borsh.deserialize(borshData, MetaplexMeta::class.java)

                    // Sometimes the borsh-deserialized data has NUL chars on the end, so we need to sanitize
                    val uri = metaplexData.data.uri.replace("\u0000", "")
                    nftUris.add(uri)
                } catch (e: Exception) {
                    Log.e("SOL", "Attached data is not Metaplex Meta format", e)
                }
            }
        } catch (e: Exception) {
            Log.e("SOL", "Error attempting to load nfts for address $address", e)
        }

        return nftUris
    }
}

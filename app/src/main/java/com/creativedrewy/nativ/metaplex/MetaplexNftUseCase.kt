package com.creativedrewy.nativ.metaplex

import android.util.Log
import com.creativedrewy.nativ.nft.NftSpecRepository
import com.creativedrewy.solanarepository.accounts.AccountRepository
import com.solana.core.PublicKey
import com.solana.core.PublicKeyRule
import com.solana.vendor.borshj.Borsh
import org.bitcoinj.core.Base58
import java.util.*
import javax.inject.Inject

class MetaplexNftUseCase @Inject constructor(
    private val accountsRepository: AccountRepository,
    private val nftSpecRepository: NftSpecRepository
) {

    suspend fun getMetaplexNftsForAccount(account: String) {
        val accountKey = PublicKey(account)

        val ownerAccounts = accountsRepository.getTokenAccountsByOwner(accountKey)

        ownerAccounts.forEach {
            val mintAddress = it.account.data.parsed.info.mint

            val pdaSeeds = listOf(
                MetaplexContstants.METADATA_NAME.toByteArray(),
                Base58.decode(MetaplexContstants.METADATA_ACCOUNT_PUBKEY),
                Base58.decode(mintAddress)
            )

            val pdaAddr = PublicKey.findProgramAddress(pdaSeeds, PublicKey(MetaplexContstants.METADATA_ACCOUNT_PUBKEY))

            val accountInfo = accountsRepository.getAccountInfo(pdaAddr.address)

            try {
                val borshData = Base64.getDecoder().decode(accountInfo.data[0])

                val borsh = Borsh()
                borsh.setRules(listOf(PublicKeyRule()))
                val metaplexData = borsh.deserialize(borshData, MetaplexMeta::class.java)

                val details = nftSpecRepository.getNftDetails(metaplexData.data.uri)

                Log.v("SOL", "Your details ${ details?.name }:")
            } catch (e: Exception) {
                Log.v("SOL", "The crash:", e)
            }
        }
    }
}
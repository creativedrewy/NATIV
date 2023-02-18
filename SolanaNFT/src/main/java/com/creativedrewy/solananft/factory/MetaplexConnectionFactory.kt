package com.creativedrewy.solananft.factory

import com.creativedrewy.solananft.BuildConfig
import com.creativedrewy.solananft.metaplex.NewJdkRpcDriver
import com.metaplex.lib.Metaplex
import com.metaplex.lib.drivers.indenty.ReadOnlyIdentityDriver
import com.metaplex.lib.drivers.solana.SolanaConnectionDriver
import com.metaplex.lib.drivers.storage.OkHttpSharedStorageDriver
import com.solana.core.PublicKey
import java.net.URL
import javax.inject.Inject

class MetaplexConnectionFactory @Inject constructor() {

    fun createMetaplexConnection(pubkey: String): Metaplex {
        val ownerPublicKey = PublicKey(pubkey)
        val solanaConnection = SolanaConnectionDriver(NewJdkRpcDriver(URL(BuildConfig.RPC_BASE_URL + BuildConfig.RPC_API_KEY)))
        val solanaIdentityDriver = ReadOnlyIdentityDriver(ownerPublicKey, solanaConnection)
        val storageDriver = OkHttpSharedStorageDriver()

        return Metaplex(solanaConnection, solanaIdentityDriver, storageDriver)
    }
}
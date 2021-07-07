package com.creativedrewy.nativ.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.creativedrewy.nativ.metaplex.MetaplexMeta
import com.creativedrewy.nativ.nft.NftSpecRepository
import com.creativedrewy.nativ.ui.theme.NATIVTheme
import com.creativedrewy.nativ.viewmodel.MainViewModel
import com.creativedrewy.solanarepository.accounts.AccountRepository
import com.solana.core.PublicKey
import com.solana.core.PublicKeyRule
import com.solana.vendor.borshj.Borsh
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.bitcoinj.core.Base58
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity(), CoroutineScope by MainScope() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NATIVTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Andrew")
                }
            }
        }

        val nftKey = PublicKey("6aEBYFt9sX1R3rPsiYWiLK1QA5vj84Sj89wC2fNLYyMw")

        val testRepository = AccountRepository()

        launch {
            val ownerAccounts = testRepository.getTokenAccountsByOwner(nftKey)

            ownerAccounts.forEach {
                val mintAddress = it.account.data.parsed.info.mint

                val pdaSeeds = listOf(
                    "metadata".toByteArray(),
                    Base58.decode("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s"),
                    Base58.decode(mintAddress)
                )

                val pdaAddr = PublicKey.findProgramAddress(pdaSeeds, PublicKey("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s"))

                val accountInfo = testRepository.getAccountInfo(pdaAddr.address)
                try {
                    val borshData = Base64.getDecoder().decode(accountInfo.data[0])

                    val borsh = Borsh()
                    borsh.setRules(listOf(PublicKeyRule()))

                    val metaplexData = borsh.deserialize(borshData, MetaplexMeta::class.java)

                    val nftRepo = NftSpecRepository()
                    val details = nftRepo.getNftDetails(metaplexData.data.uri)
                    Log.v("SOL", "Your details ${ details?.name }:")
                } catch (e: Exception) {
                    Log.e("SOL", "Err", e)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    LazyColumn() {
        items(
            count = 10
        ) {
            Text(text = "Hello $name!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NATIVTheme {
        Greeting("Android")
    }
}
package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.downloader.AssetDownloadUseCase
import com.creativedrewy.nativ.metaplex.MetaplexNftUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NftViewProps(
    val name: String = "",
    val description: String = "",
    val blockchain: Blockchain = Dev,
    val siteUrl: String = "",
    val assetType: AssetType = Model3d,
    val assetUrl: String = "",
    val mediaBytes: ByteArray = byteArrayOf()
)

sealed class AssetType

object Model3d : AssetType()
object Image: AssetType()

sealed class Blockchain(
    val name: String
)

object Dev: Blockchain("Development")
object Solana : Blockchain("Solana")

@HiltViewModel
class MainViewModel @Inject constructor(
    private val metaplexNftUseCase: MetaplexNftUseCase,
    private val assetDownloadUseCase: AssetDownloadUseCase
): ViewModel() {

    var viewState: MutableLiveData<List<NftViewProps>> = MutableLiveData(listOf())

    fun loadNfts() {
        viewModelScope.launch {
            val nftData = metaplexNftUseCase.getMetaplexNftsForAccount("6aEBYFt9sX1R3rPsiYWiLK1QA5vj84Sj89wC2fNLYyMw")

            val nftProps = nftData.map { nft ->
                return@map async {
                    NftViewProps(
                        name = nft.name,
                        description = nft.description,
                        blockchain = Solana,
                        //siteUrl = nft.externalUrl,    //TODO: Not deserializing this properly
                        assetType = if (nft.properties.category == "vr") Model3d else Image,
                        assetUrl = if (nft.properties.category != "vr") nft.image else "",
                        mediaBytes = assetDownloadUseCase.downloadAsset(nft.properties.files.first())
                    )
                }
            }.awaitAll()

            viewState.postValue(nftProps)
        }
    }

}
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

sealed class ScreenState(
    val listItems: List<NftViewProps>
)

class Empty: ScreenState(listOf())

class Loading: ScreenState(
    listOf(
        NftViewProps(
            name = "Text text text text text text text text text"
        ),
        NftViewProps(
            name = "Text text text text text text text text text"
        )
    )
)

class Display(
    val items: List<NftViewProps>
): ScreenState(items)


data class NftViewProps(
    val name: String = "",
    val description: String = "",
    val blockchain: Blockchain = Dev,
    val siteUrl: String = "",
    val assetType: AssetType = Image,
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

    var viewState: MutableLiveData<ScreenState> = MutableLiveData(Empty())

    fun loadNfts() {
        viewState.postValue(Loading())

        viewModelScope.launch {
            val nftData = metaplexNftUseCase.getMetaplexNftsForAccount("6aEBYFt9sX1R3rPsiYWiLK1QA5vj84Sj89wC2fNLYyMw")

            val nftProps = nftData.map { nft ->
                return@map async {
                    val assetBytes = assetDownloadUseCase.downloadAsset(nft.properties.files.first())

                    NftViewProps(
                        name = nft.name,
                        description = nft.description,
                        blockchain = Solana,
                        //siteUrl = nft.externalUrl,    //TODO: Not deserializing this properly
                        assetType = if (nft.properties.category == "vr") Model3d else Image,
                        assetUrl = if (nft.properties.category != "vr") nft.image else "",
                        mediaBytes = assetBytes
                    )
                }
            }.awaitAll()

            viewState.postValue(Display(nftProps))
        }
    }

}
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

@HiltViewModel
class NftGalleryViewModel @Inject constructor(
    private val metaplexNftUseCase: MetaplexNftUseCase,
    private val assetDownloadUseCase: AssetDownloadUseCase
): ViewModel() {

    var viewState: MutableLiveData<NftGalleryViewState> = MutableLiveData(Empty())

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
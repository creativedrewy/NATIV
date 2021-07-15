package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.downloader.AssetDownloadUseCase
import com.creativedrewy.nativ.metaplex.MetaplexNftUseCase
import com.creativedrewy.nativ.opensea.OpenSeaQueryUseCase
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NftGalleryViewModel @Inject constructor(
    private val metaplexNftUseCase: MetaplexNftUseCase,
    private val assetDownloadUseCase: AssetDownloadUseCase,
    private val userAddrsUseCase: UserAddressesUseCase,
    private val openSeaQueryUseCase: OpenSeaQueryUseCase
): ViewModel() {

    private val _state = MutableStateFlow<NftGalleryViewState>(Empty())

    val viewState: StateFlow<NftGalleryViewState>
        get() = _state

    init {
        loadNfts()
    }

    fun loadNfts() {
        _state.value = Loading()

        viewModelScope.launch {
            userAddrsUseCase.allUserAddresses
                .collect { addresses ->
                    val allNfts = mutableListOf<NftViewProps>()

                    addresses.forEach { chainAddr ->
                        val nftData = if (chainAddr.blockchain == "ETH") {
                            openSeaQueryUseCase.getOpenSeaNftsByAddress(chainAddr.pubKey.orEmpty())
                        } else {
                            metaplexNftUseCase.getMetaplexNftsForAccount(chainAddr.pubKey.orEmpty())
                        }

                        val nftProps = nftData.map { nft ->
                            return@map async {
                                val assetBytes = if (nft.properties.category == "vr") {
                                    assetDownloadUseCase.downloadAsset(nft.properties.files.first())
                                } else {
                                    byteArrayOf()
                                }

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

                        allNfts.addAll(nftProps)
                    }

                    _state.value = Display(allNfts)
                }
        }
    }

}
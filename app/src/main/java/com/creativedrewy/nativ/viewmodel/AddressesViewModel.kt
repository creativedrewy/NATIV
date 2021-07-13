package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.creativedrewy.nativ.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AddrViewState(
    val supportedChains: List<SupportedChain> = listOf()
)

data class SupportedChain(
    val name: String,
    val symbol: String,
    val iconRes: Int
)

@HiltViewModel
class AddressesViewModel @Inject constructor(

): ViewModel() {

    var viewState: MutableLiveData<AddrViewState> = MutableLiveData(AddrViewState())

    init {
        val chainList = listOf(
            SupportedChain(
                name = "Solana",
                symbol = "SOL",
                iconRes = R.drawable.solana_logo
            )
        )

        viewState.postValue(viewState.value?.copy(
            supportedChains = chainList
        ))
    }

    fun saveAddress() {

    }

}
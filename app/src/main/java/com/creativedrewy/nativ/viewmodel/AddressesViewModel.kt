package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.creativedrewy.nativ.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AddrViewState(
    val supportedChains: List<SupportedChain> = listOf(),
    val userAddresses: List<UserAddress> = listOf()
)

data class SupportedChain(
    val name: String,
    val symbol: String,
    val iconRes: Int
)

data class UserAddress(
    val addrPubKey: String,
    val chainDrawable: Int
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

        val item = UserAddress(
            addrPubKey = "8heEeWszgr...VEweD8YEQ",
            chainDrawable = R.drawable.solana_logo
        )
        val userAddreses = listOf(item, item, item)

        viewState.postValue(viewState.value?.copy(
            supportedChains = chainList,
            userAddresses = userAddreses
        ))
    }

    fun saveAddress() {

    }

}
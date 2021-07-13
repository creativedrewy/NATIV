package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val addressesUseCase: UserAddressesUseCase
): ViewModel() {

    val viewState: MutableLiveData<AddrViewState> = MutableLiveData(AddrViewState())

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

    fun saveAddress(symbol: String, address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            addressesUseCase.saveNewAddress(symbol, address)
        }
    }

}
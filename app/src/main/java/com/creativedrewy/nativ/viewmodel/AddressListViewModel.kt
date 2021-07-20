package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.chainsupport.ISupportedChains
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddrViewState(
    //val supportedChains: List<SupportedChain> = listOf(),
    val userAddresses: List<UserAddress> = listOf()
)

data class SupportedChain(
    val name: String,
    val ticker: String,
    val iconRes: Int
)

data class UserAddress(
    val address: String,
    val chainLogoRes: Int,
    val chainTicker: String
)

@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val addressesUseCase: UserAddressesUseCase,
    private val supportedChains: ISupportedChains
): ViewModel() {

    private val chainList = listOf(
        SupportedChain(
            name = "Solana",
            ticker = "SOL",
            iconRes = R.drawable.solana_logo
        ),
        SupportedChain(
            name = "Ethereum",
            ticker = "ETH",
            iconRes = R.drawable.eth_diamond_black
        )
    )

    private val _state = MutableStateFlow(AddrViewState())

    val viewState: StateFlow<AddrViewState>
        get() = _state

    init {
        viewModelScope.launch {
            addressesUseCase.allUserAddresses
                .collect { list ->
                    val mapped = list.map { addr ->
                        val logoRes = chainList.find { it.ticker == addr.blockchain }?.iconRes ?: -1

                        UserAddress(addr.pubKey, logoRes, addr.blockchain)
                    }

                    _state.value = AddrViewState(
                        //supportedChains = chainList,
                        userAddresses = mapped
                    )
                }
        }
    }

    fun saveAddress(address: String, ticker: String) {
        viewModelScope.launch {
            addressesUseCase.saveNewAddress(address, ticker)
        }
    }

    fun deleteAddress(address: String, ticker: String) {
        viewModelScope.launch {
            addressesUseCase.deleteAddress(address, ticker)
        }
    }
}
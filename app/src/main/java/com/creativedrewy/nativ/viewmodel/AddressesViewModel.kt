package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddrViewState(
    val supportedChains: List<SupportedChain> = listOf(),
    val userAddresses: List<UserAddress> = listOf()
)

data class SupportedChain(
    val name: String,
    val ticker: String,
    val iconRes: Int
)

data class UserAddress(
    val address: String,
    val chainLogoRes: Int
)

@HiltViewModel
class AddressesViewModel @Inject constructor(
    private val addressesUseCase: UserAddressesUseCase
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
                        val locatedRes = chainList.find { it.ticker == addr.blockchain }?.iconRes ?: -1

//                        var pubKeyAddr = addr.pubKey ?: ""
//                        pubKeyAddr = if (pubKeyAddr.length >= 20) {
//                            pubKeyAddr.take(10) + "..." +  pubKeyAddr.takeLast(10)
//                        } else {
//                            pubKeyAddr
//                        }

                        UserAddress(addr.pubKey, locatedRes)
                    }

                    _state.value = AddrViewState(
                        supportedChains = chainList,
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

    fun deleteAddress(address: String) {
        viewModelScope.launch {
            addressesUseCase.allUserAddresses.collect { addresses ->
                val foundChain = addresses.firstOrNull { it.pubKey == address }?.blockchain
                foundChain?.let {
                    addressesUseCase.deleteAddress(address, it)
                }
            }
        }
    }
}
package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _state = MutableStateFlow(AddrViewState())

    val viewState: StateFlow<AddrViewState>
        get() = _state

    init {
        val chainList = listOf(
            SupportedChain(
                name = "Solana",
                symbol = "SOL",
                iconRes = R.drawable.solana_logo
            ),
            SupportedChain(
                name = "Ethereum",
                "ETH",
                iconRes = R.drawable.eth_diamond_black
            )
        )

        viewModelScope.launch {
            addressesUseCase.allUserAddresses
                .collect { list ->
                    val mapped = list.map { addr ->
                        val locatedRes = chainList.find { it.symbol == addr.blockchain }?.iconRes ?: -1

                        var pubKeyAddr = addr.pubKey ?: ""
                        pubKeyAddr = if (pubKeyAddr.length >= 20) {
                            pubKeyAddr.take(10) + "..." +  pubKeyAddr.takeLast(10)
                        } else {
                            pubKeyAddr
                        }

                        UserAddress(pubKeyAddr, locatedRes)
                    }

                    _state.value = AddrViewState(
                        supportedChains = chainList,
                        userAddresses = mapped
                    )
                }
        }
    }

    fun saveAddress(symbol: String, address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            addressesUseCase.saveNewAddress(symbol, address)
        }
    }

}
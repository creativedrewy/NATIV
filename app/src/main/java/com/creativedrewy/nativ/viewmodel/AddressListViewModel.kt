package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.chainsupport.ISupportedChains
import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddrViewState(
    val userAddresses: List<UserAddress> = listOf(),
    val supportedChains: List<SupportedChain> = listOf()
)

data class UserAddress(
    val address: String,
    val chainLogoRes: Int,
    val chainTicker: String
)

@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val addressesUseCase: UserAddressesUseCase,
    private val chainSupport: ISupportedChains
): ViewModel() {

    private val _state = MutableStateFlow(AddrViewState())

    val viewState: StateFlow<AddrViewState>
        get() = _state

    init {
        val chainList = chainSupport.supportedChains

        viewModelScope.launch {
            addressesUseCase.allUserAddresses
                .collect { list ->
                    val mapped = list.map { addr ->
                        val logoRes = chainList.find { it.ticker == addr.blockchain }?.iconRes ?: -1

                        UserAddress(addr.pubKey, logoRes, addr.blockchain)
                    }

                    _state.value = AddrViewState(
                        userAddresses = mapped,
                        supportedChains = chainSupport.supportedChains.toList()
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
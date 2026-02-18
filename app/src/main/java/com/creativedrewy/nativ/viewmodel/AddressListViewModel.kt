package com.creativedrewy.nativ.viewmodel

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.chainsupport.ISupportedChains
import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.usecase.UserAddressesUseCase
import com.funkatronics.encoders.Base58
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.ConnectionIdentity
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

data class WalletConnectResult(
    val success: Boolean,
    val message: String
)

@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val addressesUseCase: UserAddressesUseCase,
    private val chainSupport: ISupportedChains
) : ViewModel() {

    private val _state = MutableStateFlow(AddrViewState())

    val viewState: StateFlow<AddrViewState>
        get() = _state

    private val walletAdapter = MobileWalletAdapter(
        connectionIdentity = ConnectionIdentity(
            identityUri = "https://nativ.app".toUri(),
            iconUri = "favicon.ico".toUri(),
            identityName = "NATIV"
        )
    )

    init {
        loadAddresses()
    }

    fun loadAddresses() {
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
                        supportedChains = chainSupport.supportedChains.sortedBy { it.ticker }.toList()
                    )
                }
        }
    }

    fun formatAddress(srcAddr: String): String {
        return if (srcAddr.length >= 20) {
            srcAddr.take(8) + "..." + srcAddr.takeLast(8)
        } else {
            srcAddr
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

    suspend fun connectToWallet(activityResultSender: ActivityResultSender): WalletConnectResult {
        return when (val result = walletAdapter.connect(activityResultSender)) {
            is TransactionResult.Success -> {
                val account = result.authResult.accounts.firstOrNull()
                val publicKeyBytes = account?.publicKey

                if (publicKeyBytes == null) {
                    WalletConnectResult(
                        success = false,
                        message = "Connected, but no public key was returned."
                    )
                } else {
                    val publicKeyBase58 = Base58.encodeToString(publicKeyBytes)
                    val ticker = chainSupport.supportedChains
                        .firstOrNull { it.ticker == "SOL" }
                        ?.ticker ?: "SOL"
                    addressesUseCase.saveNewAddress(publicKeyBase58, ticker)

                    WalletConnectResult(
                        success = true,
                        message = "Wallet connected."
                    )
                }
            }
            is TransactionResult.NoWalletFound -> WalletConnectResult(
                success = false,
                message = "No compatible wallet app found on this device."
            )
            is TransactionResult.Failure -> WalletConnectResult(
                success = false,
                message = "Unable to connect to wallet. Please try again."
            )
        }
    }
}

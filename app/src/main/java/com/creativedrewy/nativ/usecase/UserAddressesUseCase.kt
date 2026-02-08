package com.creativedrewy.nativ.usecase

import com.creativedrewy.nativ.database.ChainAddr
import com.creativedrewy.nativ.repository.AddressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserAddressesUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {

    val allUserAddresses = addressRepository.allUserAddresses

    suspend fun loadUserAddresses() = withContext(Dispatchers.IO) {
        addressRepository.loadAddresses()
    }

    suspend fun saveNewAddress(addr: String, ticker: String) {
        withContext(Dispatchers.IO) {
            val addrEntity = ChainAddr(addr, ticker)

            addressRepository.saveAddress(addrEntity)
        }
    }

    suspend fun deleteAddress(addr: String, ticker: String) {
        withContext(Dispatchers.IO) {
            val deleteEntry = ChainAddr(addr, ticker)

            addressRepository.deleteAddress(deleteEntry)
        }
    }
}

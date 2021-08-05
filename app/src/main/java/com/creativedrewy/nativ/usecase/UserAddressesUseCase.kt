package com.creativedrewy.nativ.usecase

import com.creativedrewy.nativ.database.ChainAddr
import com.creativedrewy.nativ.repository.DatabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserAddressesUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {

    val allUserAddresses = databaseRepository.allUserAddresses

    suspend fun loadUserAddresses() = withContext(Dispatchers.IO) {
        databaseRepository.loadAddresses()
    }

    suspend fun saveNewAddress(addr: String, ticker: String) {
        withContext(Dispatchers.IO) {
            val addrEntity = ChainAddr(addr, ticker)

            databaseRepository.saveAddress(addrEntity)
        }
    }

    suspend fun deleteAddress(addr: String, ticker: String) {
        withContext(Dispatchers.IO) {
            val deleteEntry = ChainAddr(addr, ticker)

            databaseRepository.deleteAddress(deleteEntry)
        }
    }
}

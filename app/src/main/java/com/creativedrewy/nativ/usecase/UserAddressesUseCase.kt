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

    suspend fun saveNewAddress(symbol: String, pubKey: String) {
        withContext(Dispatchers.IO) {
            val addrEntity = ChainAddr(0, pubKey, symbol)

            databaseRepository.saveAddress(addrEntity)
        }
    }

    suspend fun deleteAddress(symbol: String, addr: String) {
        withContext(Dispatchers.IO) {
            val deleteEntry = ChainAddr(-1, addr, symbol)

            databaseRepository.deleteAddress(deleteEntry)
        }
    }
}
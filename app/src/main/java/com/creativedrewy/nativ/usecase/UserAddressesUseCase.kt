package com.creativedrewy.nativ.usecase

import com.creativedrewy.nativ.database.ChainAddr
import com.creativedrewy.nativ.repository.DatabaseRepository
import javax.inject.Inject

class UserAddressesUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {

    fun loadUserStoredAddresses(): List<ChainAddr> {
        return databaseRepository.getUserAddresses()
    }

    fun saveNewAddress(symbol: String, pubKey: String) {
        val addrEntity = ChainAddr(0, symbol, pubKey)

        databaseRepository.saveAddress(addrEntity)
    }
}
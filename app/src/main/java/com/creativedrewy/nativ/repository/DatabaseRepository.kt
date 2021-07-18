package com.creativedrewy.nativ.repository

import com.creativedrewy.nativ.database.ChainAddr
import com.creativedrewy.nativ.database.ChainAddrDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val addressDao: ChainAddrDao
) {

    val allUserAddresses: Flow<List<ChainAddr>> = addressDao.getAllPubKeys()

    fun saveAddress(addr: ChainAddr) {
        addressDao.insertAll(addr)
    }

    fun deleteAddress(addr: ChainAddr) {
        addressDao.delete(addr)
    }
}
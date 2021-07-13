package com.creativedrewy.nativ.repository

import com.creativedrewy.nativ.database.ChainAddr
import com.creativedrewy.nativ.database.ChainAddrDao
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val addressDao: ChainAddrDao
) {

    fun getUserAddresses(): List<ChainAddr> {
        return addressDao.getAllPubKeys()
    }

}
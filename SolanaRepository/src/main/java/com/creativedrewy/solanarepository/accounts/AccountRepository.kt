package com.creativedrewy.solanarepository.accounts

import com.creativedrewy.solanarepository.Error
import com.creativedrewy.solanarepository.Success
import com.creativedrewy.solanarepository.rpcapi.Rpc20RequestDto
import com.creativedrewy.solanarepository.rpcapi.RpcRequestClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.solana.core.PublicKey
import com.solana.networking.RPCEndpoint
import com.solana.programs.TokenProgram
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountRepository(
    private val rpcRequestClient: RpcRequestClient = RpcRequestClient(RPCEndpoint.mainnetBetaSolana),
    private val gson: Gson = Gson(),
) {

    suspend fun getAccountInfo(accountKey: PublicKey): SimpleDataAccountDetailsDto {
        val params = mutableListOf(
            accountKey.toString(),
            hashMapOf("encoding" to "base64")
        )

        val rpcRequest = Rpc20RequestDto("getAccountInfo", params)

        return withContext(Dispatchers.IO) {
            when (val result = rpcRequestClient.makeRequest(rpcRequest)) {
                is Success -> {
                    val resultString = result.response.body?.string()

                    val typeToken = object : TypeToken<RpcResultDto<SimpleDataAccountDetailsDto>>(){}.type
                    val dto = gson.fromJson<RpcResultDto<SimpleDataAccountDetailsDto>>(resultString, typeToken)

                    dto.result.value
                }
                is Error -> {
                    SimpleDataAccountDetailsDto(listOf(), false, 0L, "", 0)
                }
            }
        }
    }

    fun getMultipleAccounts() {

    }

    suspend fun getTokenAccountsByOwner(accountKey: PublicKey, tokenProgramKey: PublicKey = TokenProgram.PROGRAM_ID): List<AccountHolderRootDto> {
        val params = mutableListOf(
            accountKey.toString(),
            hashMapOf("programId" to tokenProgramKey.toBase58()),
            hashMapOf("encoding" to "jsonParsed")
        )

        val rpcRequest = Rpc20RequestDto("getTokenAccountsByOwner", params)

        return withContext(Dispatchers.IO) {
            when (val result = rpcRequestClient.makeRequest(rpcRequest)) {
                is Success -> {
                    val resultString = result.response.body?.string()

                    val typeToken = object : TypeToken<RpcResultDto<List<AccountHolderRootDto>>>(){}.type
                    val dto = gson.fromJson<RpcResultDto<List<AccountHolderRootDto>>>(resultString, typeToken)

                    dto.result.value
                }
                is Error -> {
                    listOf()
                }
            }
        }
    }

    // To be implemented later in this repository
    fun getProgramAccounts() { }
    fun getTokenAccountBalance() { }
    fun getTokenAccountsByDelegate() { }
    fun getTokenLargestAccounts() { }
}
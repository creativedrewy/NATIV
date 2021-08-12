package com.creativedrewy.solananft.accounts

data class RpcResultDto<T>(
    val jsonrpc: String,
    val id: String,
    val result: RpcResultRootDto<T>
)

data class RpcResultRootDto<T>(
    val context: ContextData,
    val value: T
)

data class ContextData(
    val slot: Int
)

data class AccountHolderRootDto(
    val account: AccountDetailsRootDto,
    val pubkey: String
)

data class SimpleDataAccountDetailsDto(
    val data: List<String>,
    val executable: Boolean,
    val lamports: Long,
    val owner: String,
    val rentEpoch: Int,
)

data class AccountDetailsRootDto(
    val data: AccountProgramDto,
    val lamports: Long,
    val executable: Boolean,
    val owner: String,
    val rentEpoch: Int,
)

data class AccountProgramDto(
    val parsed: AccountTypeParsedDto,
    val program: String,
    val space: Int
)

data class AccountTypeParsedDto(
    val info: AccountInfoStateDto,
    val type: String
)

data class AccountInfoStateDto(
    val isNative: Boolean,
    val mint: String,
    val owner: String,
    val state: String,
    val tokenAmount: TokenAmountDetailsDto
)

data class TokenAmountDetailsDto(
    val amount: Double,
    val decimals: Double,
    val uiAmount: Double,
    val uiAmountString: Double
)

data class GlobalSupplyDto(
    val circulating: Long,
    val nonCirculating: Long,
    val nonCirculatingAccounts: List<String>,
    val total: Long
)

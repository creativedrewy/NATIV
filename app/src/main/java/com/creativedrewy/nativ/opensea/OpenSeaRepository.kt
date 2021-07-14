package com.creativedrewy.nativ.opensea

import com.creativedrewy.solanarepository.ApiRequestClient
import com.google.gson.Gson
import okhttp3.OkHttpClient

class OpenSeaRepository constructor(
    private val apiRequestClient: ApiRequestClient = ApiRequestClient(OkHttpClient()),
    private val gson: Gson = Gson()
) {


}
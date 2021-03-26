package com.mobilestudio.network

import com.mobilestudio.network.models.StationsNetworkResponse
import retrofit2.Response
import retrofit2.http.GET

interface StoresService {

    @GET("networks/ecobici")
    suspend fun getStoresPosition(): Response<StationsNetworkResponse>

}
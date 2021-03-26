package com.mobilestudio.network

import com.mobilestudio.network.models.StationsNetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.create

class ApiStores {

    private val retrofit by lazy {
        createRetrofit()
    }

    private val endpointsStore by lazy {
        retrofit.create<StoresService>()
    }


    private fun createRetrofit(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder().apply {
            interceptors().add(loggingInterceptor)
        }.build()

        return Retrofit.Builder().apply {
            baseUrl("http://api.citybik.es/v2/")
            addConverterFactory(JacksonConverterFactory.create())
            client(okHttpClient)
        }.build()
    }

    suspend fun call() {
        val stores: ResultNetwork<StationsNetworkResponse> = getStores()
        when (stores) {
            is ResultNetwork.Success -> {
                stores.data
            }
            is ResultNetwork.Failure -> {
                stores.error
            }
        }
    }

    suspend fun getStores(): ResultNetwork<StationsNetworkResponse> = withContext(Dispatchers.IO) {
        val result: Response<StationsNetworkResponse> = endpointsStore.getStoresPosition()
        return@withContext try {
            if (result.isSuccessful) {
                if (result.code() == 200) {
                    val bodyRequest = result.body()
                    if (bodyRequest != null) {
                        ResultNetwork.Success(bodyRequest)
                    } else {
                        ResultNetwork.Failure(
                            MyOwnError(
                                Throwable("No body request"),
                                result.code()
                            )
                        )
                    }
                } else {
                    //Error
                    ResultNetwork.Failure(
                        MyOwnError(
                            Throwable("Result code no 200"),
                            result.code()
                        )
                    )
                }
            } else {
                ResultNetwork.Failure(
                    MyOwnError(
                        Throwable("Network Call no successful"),
                        0
                    )
                )
            }
        } catch (exception: Exception) {
            ResultNetwork.Failure(
                MyOwnError(
                    exception,
                    0
                )
            )
        }
    }

}

sealed class ResultNetwork<out T> {
    data class Success<out T>(val data: T) : ResultNetwork<T>()
    data class Failure(val error: MyOwnError) : ResultNetwork<Nothing>()
}

data class MyOwnError(val error: Throwable, val codeError: Int)
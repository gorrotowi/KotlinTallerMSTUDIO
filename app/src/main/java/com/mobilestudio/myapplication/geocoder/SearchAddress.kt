package com.mobilestudio.myapplication.geocoder

import android.content.Context
import android.location.Geocoder
import com.mobilestudio.myapplication.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.*

class SearchAddress(private val context: WeakReference<Context>) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getAddressByLatLng(lat: Double, lng: Double): Result<String?> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val geocoder = Geocoder(context.get(), Locale.getDefault())
                val addressList = geocoder.getFromLocation(lat, lng, 1)
                val fetchAddress = addressList.firstOrNull()
                val maxAddressLine = fetchAddress?.maxAddressLineIndex ?: 0
                if (maxAddressLine >= 0) {
                    val address = fetchAddress?.getAddressLine(0)
                    Result.Success(address)
                } else {
                    Result.Failure(Throwable("No Address Line for position"))
                }
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
}
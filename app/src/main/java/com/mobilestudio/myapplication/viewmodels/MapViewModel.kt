package com.mobilestudio.myapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobilestudio.myapplication.geocoder.SearchAddress
import com.mobilestudio.myapplication.utils.Result
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val searchAddress by lazy {
        SearchAddress(WeakReference(application))
    }

    private val mutableAddressName = MutableLiveData<String?>()
    private val mutableCarnet = MutableLiveData<String?>()
    private val mutableError = MutableLiveData<String>()

    val addressName: LiveData<String?>
        get() = mutableAddressName

    val carnet: LiveData<String?>
        get() = mutableCarnet

    val error: LiveData<String?>
        get() = mutableError

    fun searchByLatLng(lat: Double, lng: Double) {
        viewModelScope.launch {
            when (val result = searchAddress.getAddressByLatLng(lat, lng)) {
                is Result.Success -> mutableAddressName.postValue(result.data)
                is Result.Failure -> mutableError.postValue(result.error.localizedMessage)
            }

        }
    }

//    val repositoryCarnet = //....

    fun searchIDContent(contentCode: String) {
//        viewModelScope.launch {
//            when (val result = repositoryCarnet.searchById(contentCode)) {
//                is Result.Success -> mutableCarnet.postValue(result.data)
//                is Result.Failure -> mutableError.postValue(result.error.localizedMessage)
//            }
//        }
    }
}
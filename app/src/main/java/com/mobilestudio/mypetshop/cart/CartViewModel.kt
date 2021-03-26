package com.mobilestudio.mypetshop.cart

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobilestudio.mypetshop.R
import com.mobilestudio.mypetshop.models.Product
import com.mobilestudio.repository.ProductsRepository
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductsRepository(application)

    private val mutableListProducts = MutableLiveData<MutableList<Product>?>()
    val listProducts: LiveData<MutableList<Product>?>
        get() = mutableListProducts

    private val mutableError: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String>
        get() = mutableError

    private val mutablePositionToDelete = MutableLiveData<Int>()
    val positionToDelete: MutableLiveData<Int>
        get() = mutablePositionToDelete

    fun getAllCartProducts() {
        Log.i("VIEWMODEL", "START CALL")
        viewModelScope.launch {
            val productListFromRepo = repository.getAllProductsInCart()
            Log.i("VIEWMODEL", "from repo result")
            Log.i("VIEWMODEL", "$productListFromRepo")
            mutableListProducts.value = productListFromRepo?.map {
                Product(it.name, "${it.price}", R.drawable.ic_app, it.id)
            }?.toMutableList()
        }
    }

    fun removeItem(positionItem: Int, idItem: Int) {
        viewModelScope.launch {
//            val productToDelete = mutableListProducts.value?.get(positionItem)
//            Log.e("MUTABLE", "${productToDelete?.id}")
//            val idToDelete = productToDelete?.id
            val result = repository.deleteProductToCart(idItem)
            if (result) {
                mutablePositionToDelete.value = positionItem
                mutableListProducts.value?.removeAt(positionItem)
            } else {
                mutableError.value = "Error al borrar el producto, intenta de nuevo"
            }
        }
    }
}
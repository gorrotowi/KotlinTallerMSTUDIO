package com.mobilestudio.repository

import android.app.Application
import android.util.Log
import com.mobilestudio.localstorage.PetChopDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.mobilestudio.localstorage.entities.Product as ProductDB

class ProductsRepository(application: Application) {

    private val db by lazy {
        PetChopDatabase.getInstance(application)
    }

    suspend fun getAllProductsInCart(): List<Product>? = withContext(Dispatchers.IO) {
        Log.i("REPO", "START CALL")
        val listProducts = db?.productDao()?.getAllProducts()?.map {
            it.toProduct()
        }
        return@withContext listProducts
    }

    private fun ProductDB.toProduct(): Product {
        return Product(id, name, price)
    }

    suspend fun addProductToCart(name: String, price: String) = withContext(Dispatchers.IO) {
        db?.productDao()?.addProduct(
            ProductDB(0, name, 100, "", "", 1, "", false)
        )
    }

    suspend fun deleteProductToCart(id: Int?): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            if (id != null) {
                db?.productDao()?.removeProductAndConfirm(ProductDB(id)) ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateQuantity(id: Int, newQuantity: Int) = withContext(Dispatchers.IO) {
        val productUpdated = db?.productDao()?.getProductById(id)?.copy(quantity = newQuantity)
        if (productUpdated != null) {
            db?.productDao()?.updateProduct(productUpdated)
        } else {
            //Error
        }
    }

}

data class Product(val id: Int, val name: String, val price: Int)
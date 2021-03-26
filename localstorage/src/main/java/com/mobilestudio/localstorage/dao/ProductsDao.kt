package com.mobilestudio.localstorage.dao

import android.util.Log
import androidx.room.*
import com.mobilestudio.localstorage.entities.Product

@Dao
interface ProductsDao {

    @Query("SELECT * FROM PetProduct")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM PetProduct WHERE id == :identifier")
    suspend fun getProductById(identifier: Int): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProduct(product: Product)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProduct(product: Product)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProducts(product: List<Product>)

    @Delete
    suspend fun removeProduct(product: Product)

    @Transaction
    suspend fun removeProductAndConfirm(product: Product): Boolean {
        removeProduct(product)
        val data = getProductById(product.id)
        Log.e("DB", "$data")
        return data == null
    }

    @Transaction
    suspend fun updateAllQuantityToNew(newQuantity: Int): List<Product> {
        val products = getAllProducts()
        val updatedProducts = products.map { prd ->
            prd.copy(quantity = newQuantity)
        }
        updateProducts(updatedProducts)
        return getAllProducts()
    }

}
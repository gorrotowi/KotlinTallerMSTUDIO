package com.mobilestudio.mypetshop.cart

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mobilestudio.mypetshop.adapters.AdapterCart
import com.mobilestudio.mypetshop.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    private val viewModel by viewModels<CartViewModel>()

    private val binding by lazy {
        ActivityCartBinding.inflate(layoutInflater)
    }

    private val adapterCart by lazy {
        AdapterCart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.getAllCartProducts()
        binding.rcvProducts.adapter = adapterCart
        setUpObservables()
        setUpActions()
    }

    private fun setUpActions() {
        adapterCart.addOnRemoveItem { positionItem, idItem ->
            viewModel.removeItem(positionItem, idItem)
        }
    }

    private fun setUpObservables() {
        viewModel.listProducts.observe(this) { products ->
            if (products != null) {
                adapterCart.setProducts(products.toMutableList())
            }
        }

        viewModel.positionToDelete.observe(this) { position ->
            adapterCart.removeItem(position)
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }


}
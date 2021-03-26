package com.mobilestudio.mypetshop.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mobilestudio.mypetshop.R
import com.mobilestudio.mypetshop.adapters.AdapterCategory
import com.mobilestudio.mypetshop.adapters.AdapterProducts
import com.mobilestudio.mypetshop.cart.CartActivity
import com.mobilestudio.mypetshop.databinding.ActivityStoreBinding
import com.mobilestudio.mypetshop.detailproduct.DetailProductActivity
import com.mobilestudio.mypetshop.models.Product

class StoreActivity : AppCompatActivity() {

    private val binding: ActivityStoreBinding by lazy {
        Log.e("MENSAJE", "HELLO LAZY!!")
        ActivityStoreBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<StoreViewModel>()

    private val adapterCategory = AdapterCategory(getCategoryProducts())
    private val adapterProducts = AdapterProducts(getFakeProducts())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpUI()
        setUpActions()
        setUpObservables()

    }

    private fun setUpUI() {
        binding.rcCategory.adapter = adapterCategory
        binding.rcBestProducts.adapter = adapterProducts
    }

    private fun setUpActions() {
        binding.btnShopCar.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        adapterProducts.setOnItemClick { product ->
            Log.e("ACTIVITY", "${product.name}")
            Log.e("ACTIVITY", "${product.price}")
//            val intent = Intent(this, DetailProductActivity::class.java)
//            intent.putExtra(DetailProductActivity.KEY_NAME, product.name)
//            intent.putExtra(DetailProductActivity.KEY_PRICE, product.price)
//            startActivity(intent)
            viewModel.addProduct(product.name, product.price)
        }
    }

    private fun setUpObservables() {
        //viewmodel observers
        viewModel.listProducts.observe(this) { listProducts ->
            listProducts?.forEach {
                Log.i("PRODUCT", it.name)
            }
        }
    }


    private fun getCategoryProducts(): List<String> {
        val listCategories = mutableListOf<String>()
        listCategories.add("Food")
        listCategories.add("Toys")
        listCategories.add("Accessories")
        return listCategories
    }

    private fun getFakeProducts(): List<Product> {
        val listProducts = (0..20).map {
            Product("Product $it", "$${it * 3.1 / 5.0}", R.drawable.ic_app)
        }
        return listProducts
    }
}
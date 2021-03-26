package com.mobilestudio.mypetshop.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobilestudio.mypetshop.R
import com.mobilestudio.mypetshop.databinding.ItemProductCartBinding
import com.mobilestudio.mypetshop.models.Product

class AdapterCart : RecyclerView.Adapter<AdapterCart.ViewHolder>() {

    private var dataSource = mutableListOf<Product>()
    private lateinit var onRemoveItemListener: (Int, Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            parent.createItemProductCartBinding()
        )
    }

    private fun ViewGroup.createItemProductCartBinding() = ItemProductCartBinding.inflate(
        LayoutInflater.from(this.context),
        this,
        false
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(data = dataSource[position])
        holder.itemProductBinding.imgItemProductCartRemove.setOnClickListener {
            Log.e("ITEM TO REMOVE", "->>> ${holder.adapterPosition}")
            onRemoveItemListener(holder.adapterPosition, dataSource[position].id)
        }
    }

    override fun getItemCount(): Int = dataSource.size


    fun setProducts(products: MutableList<Product>) {
        dataSource = products
        notifyDataSetChanged()
    }

    fun addProduct(product: Product) {
        dataSource.add(product)
        notifyItemInserted(dataSource.size)
    }

    fun removeItem(position: Int) {
        dataSource.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addOnRemoveItem(block: (Int, Int) -> Unit) {
        onRemoveItemListener = block
    }

    class ViewHolder(val itemProductBinding: ItemProductCartBinding) :
        RecyclerView.ViewHolder(itemProductBinding.root) {

        fun bindView(data: Product) {
            itemProductBinding.txtProductCartName.text = data.name
            itemProductBinding.txtProductCartPrice.text = data.price
            itemProductBinding.imgItemProductCart.setImageResource(R.drawable.ic_app)
        }
    }
}
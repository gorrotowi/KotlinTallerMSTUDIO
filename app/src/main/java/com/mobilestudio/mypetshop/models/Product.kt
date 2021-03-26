package com.mobilestudio.mypetshop.models

import androidx.annotation.DrawableRes

data class Product(val name: String, val price: String, @DrawableRes val img: Int, val id: Int = 0)

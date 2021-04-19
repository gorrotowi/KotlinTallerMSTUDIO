package com.mobilestudio.myapplication

import android.app.Application
import com.google.android.libraries.places.api.Places

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Places.initialize(this, "AIzaSyClCxWlMJJwvOEfpzrohHqh86c4upqVr8Y")
    }
}
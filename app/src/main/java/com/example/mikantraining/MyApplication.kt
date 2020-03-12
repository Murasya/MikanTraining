package com.example.mikantraining

import android.app.Application

class MyApplication: Application() {
    override fun onCreate(){
        super.onCreate()
        MyContext.onCreateApplication(applicationContext)
    }
}
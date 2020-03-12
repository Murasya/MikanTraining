package com.example.mikantraining

import com.example.mikantraining.MyContext
import android.app.Application
import android.content.Context

class MyContext private constructor(applicationContext: Context) {
    private var applicationContext: Context = applicationContext

    companion object {
        private var instance: MyContext? = null
        fun onCreateApplication(applicationContext: Context) {
            instance = MyContext(applicationContext)
        }

        fun getInstance(): MyContext {
            if (instance == null) {
                throw java.lang.RuntimeException("MyContext should be initialized")
            } else {
                return instance!!
            }
        }

        fun getContext(): Context {
            return getInstance().applicationContext
        }
    }

}
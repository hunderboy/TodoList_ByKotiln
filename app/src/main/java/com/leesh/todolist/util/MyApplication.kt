package com.leesh.todolist.util

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    init{
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null
        fun ApplicationContext() : Context {
            return instance!!.applicationContext
        }
    }

}
package com.example.splitbill

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SplitBillApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}

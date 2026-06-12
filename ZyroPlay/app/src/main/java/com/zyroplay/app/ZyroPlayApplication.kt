package com.zyroplay.app

import android.app.Application
import com.zyroplay.app.cast.CastManager

class ZyroPlayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CastManager.initialize(this)
    }
}

package com.keyvault.keyvault

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.keyvault.keyvault.utils.Utils

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val themeMode = Utils.getAppTheme(this)
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

}
package com.keyvault.keyvault

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.keyvault.keyvault.utils.Utils

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        Utils.setAppReopened(this, true)

        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(bottomNavigationBar, navController)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStop() {
        super.onStop()
        Utils.setAppReopened(this, true)
    }

    override fun onResume() {
        super.onResume()
        if (Utils.isAppReopened(this) && Utils.getAppLockPrefs(this)) {
            Utils.showBiometric(
                this,
                onSuccess = {
                    Utils.showToast(this, "Authentication successful")
                    Utils.setAppReopened(this, false)
                },
                onError = { message ->
                    Utils.showToast(this, message)
                }
            )
        }
    }
}
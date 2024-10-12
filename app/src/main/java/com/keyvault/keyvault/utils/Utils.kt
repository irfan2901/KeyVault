package com.keyvault.keyvault.utils

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.keyvault.keyvault.databinding.ProgressLayoutBinding

object Utils {

    fun saveToken(context: Context, token: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getToken(context: Context): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }

    fun removeToken(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("token").apply()
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private var dialog: AlertDialog? = null

    fun showDialog(context: Context, message: String) {
        val progress = ProgressLayoutBinding.inflate(LayoutInflater.from(context))
        progress.progressMessage.text = message
        dialog = AlertDialog.Builder(context).setView(progress.root)
            .setCancelable(false)
            .create()
        dialog!!.show()
    }

    fun hideDialog() {
        dialog?.dismiss()
        dialog = null
    }

    fun saveAppTheme(context: Context, theme: Int) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("APP_THEME", theme).apply()
    }

    fun getAppTheme(context: Context): Int {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("APP_THEME", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun showBiometric(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        if (BiometricManager.from(activity)
                .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
        ) {
            val executor = ContextCompat.getMainExecutor(activity)

            val biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        when (errorCode) {
                            BiometricPrompt.ERROR_USER_CANCELED,
                            BiometricPrompt.ERROR_CANCELED -> {
                                activity.finish()
                            }

                            else -> {
                                onError("Authentication error: $errString")
                                activity.finish()
                            }
                        }
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Verify your fingerprint")
                .setNegativeButtonText("cancel")
                .build()

            biometricPrompt.authenticate(promptInfo)
        } else {
            onError("Authentication failed")
            activity.finish()
        }
    }

    fun saveAppLockPrefs(context: Context, isLocked: Boolean) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("BiometricPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("BIOMETRIC_ENABLED", isLocked).apply()
    }

    fun getAppLockPrefs(context: Context): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("BiometricPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("BIOMETRIC_ENABLED", false)
    }

    fun setAppReopened(context: Context, isReopened: Boolean) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("APP_REOPENED", isReopened).apply()
    }

    fun isAppReopened(context: Context): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("APP_REOPENED", false)
    }

    val categories = arrayOf(
        "All",
        "Social Media",
        "Entertainment",
        "E-commerce",
        "Banking",
        "Work",
        "Travel",
        "Health",
        "Gaming",
        "Cloud Services",
        "Utilities"
    )

}
package com.keyvault.keyvault.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.keyvault.keyvault.AuthActivity
import com.keyvault.keyvault.databinding.FragmentProfileBinding
import com.keyvault.keyvault.utils.Utils
import com.keyvault.keyvault.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userViewModel: UserViewModel
    private var isBiometricLockEnabled: Boolean = false
    private var selectedTheme: Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        isBiometricLockEnabled = Utils.getAppLockPrefs(requireContext())
        binding.biometricSwitch.isChecked = isBiometricLockEnabled
        binding.biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Utils.showBiometric(requireActivity(), onSuccess = {
                    Utils.showToast(requireContext(), "Authentication successful")
                    Utils.saveAppLockPrefs(requireContext(), true)
                },
                    onError = { message ->
                        Utils.showToast(requireContext(), message)
                    })
            } else {
                disableAppLock()
            }
        }

        showUser()

        binding.logoutButton.setOnClickListener {
            Utils.removeToken(requireContext())
            startActivity(Intent(requireActivity(), AuthActivity::class.java))
            requireActivity().finish()
        }

        binding.changeThemeLayout.setOnClickListener {
            showThemeChangeDialog()
        }
    }

    private fun showUser() {
        userViewModel.showCurrentUser(requireContext())

        lifecycleScope.launch {
            userViewModel.user.collect { user ->
                user?.let {
                    val cardName = it.userName?.get(0)
                    binding.cardUsername.text = cardName.toString()
                    binding.accountUsername.text = it.userName
                    binding.accountEmail.text = it.email
                }
            }
        }
    }

    private fun showThemeChangeDialog() {
        val currentTheme = Utils.getAppTheme(requireContext())
        selectedTheme = currentTheme

        val options = arrayOf("System Default", "Dark", "Light")
        val selectedOptions = when (currentTheme) {
            AppCompatDelegate.MODE_NIGHT_YES -> 1
            AppCompatDelegate.MODE_NIGHT_NO -> 2
            else -> 0
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose theme")
            .setSingleChoiceItems(options, selectedOptions) { _, which ->
                selectedTheme = when (which) {
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    2 -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            }
            .setPositiveButton("Ok") { dialog, _ ->
                Utils.saveAppTheme(requireContext(), selectedTheme)
                AppCompatDelegate.setDefaultNightMode(selectedTheme)
                requireActivity().recreate()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun disableAppLock() {
        isBiometricLockEnabled = false
        Utils.saveAppLockPrefs(requireContext(), false)
    }
}
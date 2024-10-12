package com.keyvault.keyvault.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.keyvault.keyvault.MainActivity
import com.keyvault.keyvault.databinding.FragmentRegisterBinding
import com.keyvault.keyvault.models.UserModel
import com.keyvault.keyvault.utils.Utils
import com.keyvault.keyvault.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        authViewModel.isRegistered.observe(viewLifecycleOwner) { success ->
            Utils.hideDialog()

            if (success) {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
        }

        binding.registerButton.setOnClickListener {
            registerUser()
        }

        binding.loginTextView.setOnClickListener {
            navController.navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
    }

    private fun registerUser() {
        val username = binding.registerUsernameEt.text.toString()
        val email = binding.registerEmailEt.text.toString()
        val password = binding.registerPasswordEt.text.toString()

        var isValid = true
        binding.registerUsernameLayout.error = null
        binding.registerEmailLayout.error = null
        binding.registerPasswordLayout.error = null

        if (username.isEmpty()) {
            binding.registerUsernameLayout.error = "Username cannot be empty"
            isValid = false
        }
        if (email.isEmpty()) {
            binding.registerEmailLayout.error = "Email cannot be empty"
            isValid = false
        }
        if (password.isEmpty()) {
            binding.registerPasswordLayout.error = "Password cannot be empty"
            isValid = false
        }

        if (isValid) {
            Utils.showDialog(requireContext(), "Registering...")
            authViewModel.registerUser(
                requireContext(),
                UserModel(userName = username, email = email, password = password)
            )
        }
    }
}
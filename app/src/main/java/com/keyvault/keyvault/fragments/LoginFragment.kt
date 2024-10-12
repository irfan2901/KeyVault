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
import com.keyvault.keyvault.databinding.FragmentLoginBinding
import com.keyvault.keyvault.models.LoginModel
import com.keyvault.keyvault.utils.Utils
import com.keyvault.keyvault.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        val token = Utils.getToken(requireContext())

        if (token != null) {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
            return
        }

        authViewModel.authState.observe(viewLifecycleOwner) { success ->
            Utils.hideDialog()

            if (success) {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
        }

        binding.loginButton.setOnClickListener {
            handleLogin()
        }

        binding.registerTextView.setOnClickListener {
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }

    private fun handleLogin() {
        val email = binding.loginEmailEt.text.toString()
        val password = binding.loginPasswordEt.text.toString()

        binding.loginEmailLayout.error = null
        binding.loginPasswordLayout.error = null

        var isValid = true

        if (email.isEmpty()) {
            binding.loginEmailLayout.error = "Invalid email"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.loginPasswordLayout.error = "Invalid password"
            isValid = false
        }

        if (isValid) {
            Utils.showDialog(requireContext(), "Loading...")
            authViewModel.loginUser(
                requireContext(),
                LoginModel(email = email, password = password)
            )
        }
    }
}
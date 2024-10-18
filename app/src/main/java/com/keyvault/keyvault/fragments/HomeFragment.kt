package com.keyvault.keyvault.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.keyvault.keyvault.adapters.HomeAdapter
import com.keyvault.keyvault.databinding.FragmentHomeBinding
import com.keyvault.keyvault.utils.Utils
import com.keyvault.keyvault.viewmodel.PasswordViewModel
import com.keyvault.keyvault.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var passwordViewModel: PasswordViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var navController: NavController
    private var userId: Int? = null
    private var isBiometricLockEnabled: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        passwordViewModel = ViewModelProvider(requireActivity())[PasswordViewModel::class.java]
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        isBiometricLockEnabled = Utils.getAppLockPrefs(requireContext())

        if (isBiometricLockEnabled && Utils.isAppReopened(requireContext())) {
            binding.root.postDelayed({
                Utils.showBiometric(
                    requireActivity(),
                    onSuccess = {
                        Utils.showToast(requireContext(), "Authentication successful")
                        Utils.setAppReopened(requireContext(), false)
                    },
                    onError = { message ->
                        Utils.showToast(requireContext(), message)
                    }
                )
            }, 200)
        }

        setupRecyclerView()
        fetchCurrentUser()

        binding.floatingActionButton.setOnClickListener {
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToSaveOrUpdatePasswordFragment())
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            fetchCurrentUser()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        fetchCurrentUser()
    }

    private fun setupRecyclerView() {
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        homeAdapter = HomeAdapter { passwordModel ->
            navController.navigate(
                HomeFragmentDirections.actionHomeFragmentToSaveOrUpdatePasswordFragment(
                    passwordModel
                )
            )
        }
        binding.homeRecyclerView.adapter = homeAdapter
    }

    private fun fetchCurrentUser() {
        userViewModel.showCurrentUser(requireContext())

        lifecycleScope.launch {
            userViewModel.user.collect { userModel ->
                userModel?.let {
                    userId = it.userId ?: 0
                    binding.usernameTv.text = it.userName
                    observeDataChanges()
                }
            }
        }
    }

    private fun observeDataChanges() {
        if (userId != null && userId!! > 0) {
            passwordViewModel.showAllPasswords(requireContext(), userId!!)
            lifecycleScope.launch {
                passwordViewModel.passwords.collect { passwords ->
                    passwords.let {

                        passwords.forEach { password ->
                            Log.d("HomeFragment", "Password: ${password.accountName}")
                        }

                        homeAdapter.submitList(it.toList())
                        binding.homeRecyclerView.scrollToPosition(0)
                    }
                }
            }
        }
    }
}
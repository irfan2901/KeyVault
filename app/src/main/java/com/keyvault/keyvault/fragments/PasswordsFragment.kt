package com.keyvault.keyvault.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.keyvault.keyvault.adapters.CategoryAdapter
import com.keyvault.keyvault.adapters.PasswordAdapter
import com.keyvault.keyvault.databinding.FragmentPasswordsBinding
import com.keyvault.keyvault.viewmodel.CategoriesViewModel
import com.keyvault.keyvault.viewmodel.PasswordViewModel
import com.keyvault.keyvault.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class PasswordsFragment : Fragment() {
    private lateinit var binding: FragmentPasswordsBinding
    private lateinit var navController: NavController
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var passwordAdapter: PasswordAdapter
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var passwordViewModel: PasswordViewModel
    private lateinit var userViewModel: UserViewModel
    private var categoryName: String? = null
    private var userId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        categoriesViewModel = ViewModelProvider(requireActivity())[CategoriesViewModel::class.java]
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        passwordViewModel = ViewModelProvider(requireActivity())[PasswordViewModel::class.java]

        setupCategoriesRecyclerView()
        setupPasswordRecyclerView()

        fetchCurrentUser()

        observeCategoryDataChanges()
        observePasswordDataChanges()
    }

    override fun onResume() {
        super.onResume()
        fetchCurrentUser()
    }

    private fun fetchCurrentUser() {
        userViewModel.showCurrentUser(requireContext())
        lifecycleScope.launch {
            userViewModel.user.collect { userModel ->
                userModel?.let {
                    userId = userModel.userId ?: 0
                    if (userId!! > 0) {
                        observePasswordDataChanges()
                    }
                }
            }
        }
    }

    private fun setupCategoriesRecyclerView() {
        binding.categoriesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter { categoryModel ->
            categoryName = categoryModel.categoryName.toString()
            if (categoryName == "All") {
                clearCategoryFilter()
            } else {
                observeSpecificCategoryPasswords(categoryName!!)
            }
        }
        binding.categoriesRecyclerView.adapter = categoryAdapter
    }

    private fun setupPasswordRecyclerView() {
        binding.passwordsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        passwordAdapter = PasswordAdapter { passwordModel ->
            navController.navigate(
                PasswordsFragmentDirections.actionPasswordsFragmentToSaveOrUpdatePasswordFragment(
                    passwordModel
                )
            )
        }
        binding.passwordsRecyclerView.adapter = passwordAdapter
    }

    private fun observeCategoryDataChanges() {
        categoriesViewModel.showAllCategories(requireContext())
        lifecycleScope.launch {
            categoriesViewModel.categories.collect { categories ->
                categoryAdapter.submitList(categories)
            }
        }
    }

    private fun observePasswordDataChanges() {
        if (userId != null && userId!! > 0) {
            passwordViewModel.showAllPasswords(requireContext(), userId!!)
            lifecycleScope.launch {
                passwordViewModel.passwords.collect { passwords ->
                    passwordAdapter.submitList(passwords.toList())
                    binding.passwordsRecyclerView.scrollToPosition(0)
                }
            }
        }
    }

    private fun observeSpecificCategoryPasswords(categoryName: String) {
        passwordViewModel.getSpecificCategoryPasswords(requireContext(), categoryName)
        lifecycleScope.launch {
            passwordViewModel.categoryPasswords.collect { categoryPasswords ->
                passwordAdapter.submitList(categoryPasswords.toList())
                binding.passwordsRecyclerView.scrollToPosition(0)
            }
        }
    }

    private fun clearCategoryFilter() {
        categoryName = null
        observePasswordDataChanges()
    }
}
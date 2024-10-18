package com.keyvault.keyvault.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.keyvault.keyvault.adapters.CategoryAdapter
import com.keyvault.keyvault.databinding.FragmentSaveOrUpdatePasswordBinding
import com.keyvault.keyvault.models.AddPassword
import com.keyvault.keyvault.utils.Utils
import com.keyvault.keyvault.viewmodel.CategoriesViewModel
import com.keyvault.keyvault.viewmodel.PasswordViewModel
import com.keyvault.keyvault.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class SaveOrUpdatePasswordFragment : Fragment() {
    private lateinit var binding: FragmentSaveOrUpdatePasswordBinding
    private lateinit var navController: NavController
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var passwordViewModel: PasswordViewModel
    private lateinit var userViewModel: UserViewModel
    private val args: SaveOrUpdatePasswordFragmentArgs by navArgs()
    private var categoryName: String? = null
    private var userId: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveOrUpdatePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        categoriesViewModel = ViewModelProvider(requireActivity())[CategoriesViewModel::class.java]
        passwordViewModel = ViewModelProvider(requireActivity())[PasswordViewModel::class.java]
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        showRecyclerView()
        setupRecyclerView()
        observeDataChanges()
        fetchCurrentUser()
        showAllPasswords()
        showOrHideDeleteButton()

        binding.backArrow.setOnClickListener {
            navController.popBackStack()
        }

        binding.saveButton.setOnClickListener {
            saveAccount()
        }

        binding.deleteButton.setOnClickListener {
            deleteAccount()
        }

        lifecycleScope.launch {
            passwordViewModel.isSuccessfullyAdded.collect { success ->
                if (success) {
                    passwordViewModel.resetSuccessState()
                    navController.popBackStack()
                }
            }
        }

        lifecycleScope.launch {
            passwordViewModel.isSuccessfullyUpdated.collect { success ->
                if (success) {
                    passwordViewModel.resetSuccessState()
                    navController.popBackStack()
                }
            }
        }

        lifecycleScope.launch {
            passwordViewModel.isSuccessfullyDeleted.collect { success ->
                if (success) {
                    passwordViewModel.resetSuccessState()
                    navController.popBackStack()
                }
            }
        }
    }

    private fun showRecyclerView() {
        val password = args.Password
        binding.categoryRecyclerView.visibility = if (password == null) View.VISIBLE else View.GONE
    }

    private fun showOrHideDeleteButton() {
        val password = args.Password
        binding.deleteButton.visibility = if (password == null) View.GONE else View.VISIBLE
    }

    private fun setupRecyclerView() {
        binding.categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter { categoryModel ->
            categoryName = categoryModel.categoryName.toString()
        }
        binding.categoryRecyclerView.adapter = categoryAdapter
    }

    private fun observeDataChanges() {
        categoriesViewModel.showAllCategories(requireContext())

        lifecycleScope.launch {
            categoriesViewModel.categories.collect { categoriesModel ->
                categoryAdapter.submitList(categoriesModel)
            }
        }
    }

    private fun fetchCurrentUser() {
        userViewModel.showCurrentUser(requireContext())

        lifecycleScope.launch {
            userViewModel.user.collect { userModel ->
                userId = userModel?.userId
            }
        }
    }

    private fun saveAccount() {
        val passwords = args.Password

        if (passwords == null) {
            saveNewAccount()
        } else {
            updateAccount()
        }
    }

    private fun showAllPasswords() {
        val passwords = args.Password

        passwords?.let {
            val password = it.accountName?.get(0)
            val categoryId = it.categoryId
            binding.cardView.visibility = View.VISIBLE
            binding.cardAccountName.text = password.toString()
            binding.accountNameEt.setText(it.accountName)
            binding.accountIdEt.setText(it.accountId)
            binding.accountPasswordEt.setText(it.accountPassword)
            binding.spinnerItems.visibility = View.VISIBLE

            val categories = Utils.categories
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerItems.adapter = adapter

            passwordViewModel.getCategoryName(categoryId!!)
            lifecycleScope.launch {
                passwordViewModel.categoryName.collect { categoryNameModel ->
                    categoryName = categoryNameModel?.categoryName

                    val selectedPosition = categories.indexOf(categoryName)
                    if (selectedPosition >= 0) {
                        binding.spinnerItems.setSelection(selectedPosition)
                    }
                }
            }

            binding.spinnerItems.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        categoryName = categories[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }

        } ?: run {
            binding.cardView.visibility = View.GONE
        }
    }

    private fun saveNewAccount() {
        val accountName = binding.accountNameEt.text.toString()
        val accountId = binding.accountIdEt.text.toString()
        val password = binding.accountPasswordEt.text.toString()

        var isValid = true
        binding.accountNameLayout.error = null
        binding.accountIdLayout.error = null
        binding.accountPasswordLayout.error = null

        if (accountName.isEmpty()) {
            binding.accountNameLayout.error = "Invalid account name"
            isValid = false
        }
        if (accountId.isEmpty()) {
            binding.accountIdLayout.error = "Invalid account id"
            isValid = false
        }
        if (password.isEmpty()) {
            binding.accountPasswordLayout.error = "Invalid password"
            isValid = false
        }

        if (isValid) {
            if (categoryName != null && categoryName != "All") {
                val addPassword = AddPassword(
                    userId = userId.toString(),
                    accountName = accountName,
                    accountId = accountId,
                    accountPassword = password
                )
                passwordViewModel.addPasswordToSpecificCategory(
                    requireContext(),
                    categoryName!!,
                    userId!!,
                    addPassword
                )
            } else {
                Utils.showToast(requireContext(), "Invalid category")
            }
        }
    }

    private fun updateAccount() {
        val accountName = binding.accountNameEt.text.toString()
        val accountId = binding.accountIdEt.text.toString()
        val password = binding.accountPasswordEt.text.toString()

        val passwords = args.Password
        val id = passwords?.passwordId

        val addPassword = AddPassword(
            userId = userId.toString(),
            accountName = accountName,
            accountId = accountId,
            accountPassword = password
        )

        if (categoryName != null && categoryName != "All") {
            passwordViewModel.updateSpecificCategoryPassword(
                requireContext(),
                categoryName!!,
                id!!,
                userId!!,
                addPassword
            )
        } else {
            Utils.showToast(requireContext(), "Invalid category")
        }
    }

    private fun deleteAccount() {
        val password = args.Password
        passwordViewModel.deleteAccountPassword(
            requireContext(),
            categoryName!!,
            password?.passwordId!!,
            password.userId!!
        )
    }
}
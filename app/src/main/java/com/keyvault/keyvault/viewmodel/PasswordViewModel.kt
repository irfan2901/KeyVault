package com.keyvault.keyvault.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.keyvault.keyvault.api.RetrofitClient
import com.keyvault.keyvault.models.AddPassword
import com.keyvault.keyvault.models.CategoryName
import com.keyvault.keyvault.models.PasswordModel
import com.keyvault.keyvault.utils.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordViewModel : ViewModel() {

    private val _passwords = MutableStateFlow<List<PasswordModel>>(emptyList())
    val passwords: Flow<List<PasswordModel>> = _passwords.asStateFlow()

    private val _categoryPasswords = MutableStateFlow<List<PasswordModel>>(emptyList())
    val categoryPasswords: Flow<List<PasswordModel>> = _categoryPasswords.asStateFlow()

    private val _isSuccessfullyAdded = MutableStateFlow(false)
    val isSuccessfullyAdded: Flow<Boolean> = _isSuccessfullyAdded.asStateFlow()

    private val _isSuccessfullyUpdated = MutableStateFlow(false)
    val isSuccessfullyUpdated: Flow<Boolean> = _isSuccessfullyUpdated.asStateFlow()

    private val _categoryName = MutableStateFlow<CategoryName?>(null)
    val categoryName: Flow<CategoryName?> = _categoryName.asStateFlow()

    private val _isSuccessfullyDeleted = MutableStateFlow(false)
    val isSuccessfullyDeleted: Flow<Boolean> = _isSuccessfullyDeleted.asStateFlow()


    fun showAllPasswords(context: Context, userId: Int) {
        val token = Utils.getToken(context)
        val authToken = "Bearer $token"
        RetrofitClient.apiService.showAllPasswords(authToken, userId)
            .enqueue(object : Callback<List<PasswordModel>> {
                override fun onResponse(
                    call: Call<List<PasswordModel>>,
                    response: Response<List<PasswordModel>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _passwords.value = it
                            Log.d("PasswordViewModel", "Fetched passwords: $it")
                        } ?: run {
                            Log.d("PasswordViewModel", "null")
                        }
                    }
                }

                override fun onFailure(call: Call<List<PasswordModel>>, t: Throwable) {

                }
            })
    }

    fun getSpecificCategoryPasswords(context: Context, categoryName: String) {
        val token = Utils.getToken(context)
        val authToken = "Bearer $token"

        RetrofitClient.apiService.getSpecificCategoryPasswords(authToken, categoryName)
            .enqueue(object : Callback<List<PasswordModel>> {
                override fun onResponse(
                    call: Call<List<PasswordModel>>,
                    response: Response<List<PasswordModel>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _categoryPasswords.value = it
                        }
                    }
                }

                override fun onFailure(p0: Call<List<PasswordModel>>, p1: Throwable) {

                }

            })
    }

    fun addPasswordToSpecificCategory(
        context: Context,
        categoryName: String,
        userId: Int,
        addPassword: AddPassword
    ) {
        val token = Utils.getToken(context)
        val authToken = "Bearer $token"

        RetrofitClient.apiService.createPasswordEntry(authToken, categoryName, addPassword)
            .enqueue(object : Callback<AddPassword> {
                override fun onResponse(call: Call<AddPassword>, response: Response<AddPassword>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _isSuccessfullyAdded.value = true
                            Utils.showToast(context, "Account added...")
                            showAllPasswords(context, userId)
                        }
                    }
                }

                override fun onFailure(p0: Call<AddPassword>, t: Throwable) {
                    _isSuccessfullyAdded.value = false
                    Utils.showToast(context, "Failed: ${t.message.toString()}")
                }

            })
    }

    fun updateSpecificCategoryPassword(
        context: Context,
        categoryName: String,
        passwordId: Int,
        userId: Int,
        addPassword: AddPassword
    ) {
        val token = Utils.getToken(context)
        val authToken = "Bearer $token"

        RetrofitClient.apiService.updateAccountPassword(
            authToken,
            categoryName,
            passwordId,
            addPassword
        ).enqueue(object : Callback<AddPassword> {
            override fun onResponse(call: Call<AddPassword>, response: Response<AddPassword>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _isSuccessfullyUpdated.value = true
                        Utils.showToast(context, "Account updated...")
                        showAllPasswords(context, userId)
                    }
                }
            }

            override fun onFailure(p0: Call<AddPassword>, t: Throwable) {
                _isSuccessfullyAdded.value = false
                Utils.showToast(context, "Failed: ${t.message.toString()}")
            }

        })
    }

    fun getCategoryName(categoryId: Int) {
        RetrofitClient.apiService.getCategoryName(categoryId)
            .enqueue(object : Callback<CategoryName> {
                override fun onResponse(
                    call: Call<CategoryName>,
                    response: Response<CategoryName>
                ) {

                    if (response.isSuccessful) {
                        response.body()?.let {
                            _categoryName.value = it
                        }
                    }
                }

                override fun onFailure(p0: Call<CategoryName>, p1: Throwable) {

                }

            })
    }

    fun deleteAccountPassword(context: Context, categoryName: String, passwordId: Int, userId: Int) {
        val token = Utils.getToken(context)
        val authToken = "Bearer $token"

        RetrofitClient.apiService.deleteAccountPassword(authToken, categoryName, passwordId).enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    _isSuccessfullyDeleted.value = true
                    Utils.showToast(context, "Account deleted...")
                    showAllPasswords(context, userId)
                } else {
                    _isSuccessfullyDeleted.value = false
                }
            }

            override fun onFailure(p0: Call<String>, t: Throwable) {
                _isSuccessfullyDeleted.value = false
                Utils.showToast(context, "Failed: ${t.message.toString()}")
            }

        })
    }

    fun resetSuccessState() {
        _isSuccessfullyAdded.value = false
        _isSuccessfullyUpdated.value = false
        _isSuccessfullyDeleted.value = false
    }
}
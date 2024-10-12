package com.keyvault.keyvault.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.keyvault.keyvault.api.RetrofitClient
import com.keyvault.keyvault.models.CategoryModel
import com.keyvault.keyvault.utils.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesViewModel : ViewModel() {

    private var _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    var categories: Flow<List<CategoryModel>> = _categories.asStateFlow()

    fun showAllCategories(context: Context) {
        RetrofitClient.apiService.getCategories().enqueue(object : Callback<List<CategoryModel>> {
            override fun onResponse(
                call: Call<List<CategoryModel>>,
                response: Response<List<CategoryModel>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _categories.value = it
                    } ?: run {

                    }
                } else {
                    Utils.showToast(context, "Failed to load categories: ${response.message()}")
                }
            }

            override fun onFailure(p0: Call<List<CategoryModel>>, p1: Throwable) {

            }

        })
    }

}
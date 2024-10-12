package com.keyvault.keyvault.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.keyvault.keyvault.api.RetrofitClient
import com.keyvault.keyvault.models.UserModel
import com.keyvault.keyvault.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user.asStateFlow()

    fun showCurrentUser(context: Context) {
        val token = Utils.getToken(context)
        Log.d("UserViewModel", "Token retrieved: $token")

        if (token != null) {
            val authToken = "Bearer $token"
            RetrofitClient.apiService.showCurrentUser(authToken)
                .enqueue(object : Callback<UserModel> {
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if (response.isSuccessful) {
                            response.body()?.let { userModel ->
                                _user.value = userModel
                                Log.d("UserViewModel", "User fetched: $userModel")
                            } ?: run {
                                Log.d("UserViewModel", "Response body is null")
                                _user.value = null
                            }
                        } else {
                            Log.d(
                                "UserViewModel",
                                "Response unsuccessful: ${response.errorBody()?.string()}"
                            )
                            _user.value = null
                        }
                    }

                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        Log.d("UserViewModel", "API call failed: ${t.message}")
                        _user.value = null
                        Utils.showToast(context, "Failed: ${t.message.toString()}")
                    }
                })
        } else {
            Log.d("UserViewModel", "Token is null")
            _user.value = null
        }
    }
}
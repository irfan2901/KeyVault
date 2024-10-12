package com.keyvault.keyvault.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.keyvault.keyvault.api.RetrofitClient
import com.keyvault.keyvault.models.LoginModel
import com.keyvault.keyvault.models.LoginResponse
import com.keyvault.keyvault.models.UserModel
import com.keyvault.keyvault.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {

    private val _authState = MutableLiveData(false)
    val authState: LiveData<Boolean> get() = _authState

    private val _isRegistered = MutableLiveData(false)
    val isRegistered: LiveData<Boolean> get() = _isRegistered

    fun registerUser(context: Context, userModel: UserModel) {
        RetrofitClient.apiService.registerUser(userModel).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    token?.let {
                        Utils.saveToken(context, it)
                        _isRegistered.postValue(true)
                    } ?: run {
                        _isRegistered.postValue(false)
                    }
                } else {
                    _isRegistered.postValue(false)
                }
            }

            override fun onFailure(p0: Call<LoginResponse>, p1: Throwable) {
                _isRegistered.postValue(false)
            }

        })
    }

    fun loginUser(context: Context, userModel: LoginModel) {
        RetrofitClient.apiService.loginUser(userModel).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                if (response.isSuccessful) {
                    val token = response.body()?.token
                    token?.let {
                        Utils.saveToken(context, it)
                        _authState.postValue(true)
                    } ?: run {
                        _authState.postValue(false)
                    }
                } else {
                    Log.e("AuthViewModel", "Login failed: ${response.errorBody()?.string()}")
                    _authState.postValue(false)
                }
            }

            override fun onFailure(p0: Call<LoginResponse>, t: Throwable) {
                Log.e("AuthViewModel", "Failure: ${t.message}")
                _authState.postValue(false)
            }

        })
    }

}
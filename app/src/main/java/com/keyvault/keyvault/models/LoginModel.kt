package com.keyvault.keyvault.models

import com.google.gson.annotations.SerializedName

data class LoginModel(

    @SerializedName("Email") val email: String? = null,
    @SerializedName("Password") val password: String? = null,

    )

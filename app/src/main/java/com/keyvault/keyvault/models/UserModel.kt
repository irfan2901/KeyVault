package com.keyvault.keyvault.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class UserModel(

    @SerializedName("UserID") val userId: Int? = null,
    @SerializedName("UserName") val userName: String? = null,
    @SerializedName("Email") val email: String? = null,
    @SerializedName("Password") val password: String? = null,
    @SerializedName("CreatedAt") val createdAt: String? = null,
    @SerializedName("UpdatedAt") val updatedAt: String? = null

)

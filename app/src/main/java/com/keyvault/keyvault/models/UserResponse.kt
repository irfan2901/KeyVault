package com.keyvault.keyvault.models

import com.google.gson.annotations.SerializedName

data class UserResponse(

    @SerializedName("message") val message: String? = null,
//    @SerializedName("user") val user: UserModel? = null

)

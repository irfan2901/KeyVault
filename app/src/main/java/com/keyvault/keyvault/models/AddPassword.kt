package com.keyvault.keyvault.models

import com.google.gson.annotations.SerializedName

data class AddPassword(

    @SerializedName("UserId") val userId: String? = null,
    @SerializedName("AccountName") val accountName: String? = null,
    @SerializedName("AccountId") val accountId: String? = null,
    @SerializedName("AccountPassword") val accountPassword: String? = null

)

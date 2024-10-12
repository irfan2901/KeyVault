package com.keyvault.keyvault.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PasswordModel(

    @SerializedName("PasswordId") val passwordId: Int? = null,
    @SerializedName("UserId") val userId: Int? = null,
    @SerializedName("CategoryId") val categoryId: Int? = null,
    @SerializedName("AccountName") val accountName: String? = null,
    @SerializedName("AccountId") val accountId: String? = null,
    @SerializedName("AccountPassword") val accountPassword: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null

): Serializable

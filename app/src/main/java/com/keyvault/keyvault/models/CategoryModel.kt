package com.keyvault.keyvault.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CategoryModel(

    @SerializedName("CategoryId") val categoryId: Int?,
    @SerializedName("CategoryName") val categoryName: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?

)

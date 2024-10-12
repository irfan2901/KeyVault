package com.keyvault.keyvault.models

import com.google.gson.annotations.SerializedName

data class CategoryName(

    @SerializedName("CategoryId") val categoryId: Int? = null,
    @SerializedName("CategoryName") val categoryName: String? = null

)

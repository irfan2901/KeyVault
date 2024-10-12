package com.keyvault.keyvault.api

import com.keyvault.keyvault.models.AddPassword
import com.keyvault.keyvault.models.CategoryModel
import com.keyvault.keyvault.models.CategoryName
import com.keyvault.keyvault.models.LoginModel
import com.keyvault.keyvault.models.LoginResponse
import com.keyvault.keyvault.models.PasswordModel
import com.keyvault.keyvault.models.UserModel
import com.keyvault.keyvault.models.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("/api/users/register")
    fun registerUser(@Body user: UserModel): Call<LoginResponse>

    @POST("/api/users/login")
    fun loginUser(@Body user: LoginModel): Call<LoginResponse>

    @GET("/api/users/me")
    fun showCurrentUser(@Header("Authorization") authHeader: String): Call<UserModel>

    @PUT("/api/users/{UserId}")
    fun updateUser(
        @Header("Authorization") authHeader: String,
        @Path("UserId") userId: Int,
        @Body user: UserModel
    ): Call<UserResponse>

    @DELETE("/api/users/delete/{UserId}")
    fun deleteUser(
        @Header("Authorization") authHeader: String,
        @Path("UserId") userId: Int
    ): Call<String>

//    @PUT("/api/users/change-password")
//    fun changePassword(
//        @Header("Authorization") token: String,
//        @Body password: ChangePasswordRequest
//    ): Call<ChangePasswordRequest>

    @GET("/api/categories")
    fun getCategories(): Call<List<CategoryModel>>

    @GET("/api/categories/{CategoryId}")
    fun getCategoryName(@Path("CategoryId") categoryId: Int): Call<CategoryName>

    @GET("/api/passwords/allpasswords/{UserId}")
    fun showAllPasswords(
        @Header("Authorization") authHeader: String,
        @Path("UserId") userId: Int
    ): Call<List<PasswordModel>>

    @POST("/api/passwords/{CategoryName}")
    fun createPasswordEntry(
        @Header("Authorization") authHeader: String,
        @Path("CategoryName") categoryName: String,
        @Body addPassword: AddPassword
    ): Call<AddPassword>

    @GET("/api/passwords/{CategoryName}")
    fun getSpecificCategoryPasswords(
        @Header("Authorization") authHeader: String,
        @Path("CategoryName") categoryName: String
    ): Call<List<PasswordModel>>

    @PUT("/api/passwords/{CategoryName}/{PasswordId}")
    fun updateAccountPassword(
        @Header("Authorization") authHeader: String,
        @Path("CategoryName") categoryName: String,
        @Path("PasswordId") passwordId: Int,
        @Body addPassword: AddPassword
    ): Call<AddPassword>

    @DELETE("/api/passwords/{CategoryName}/{PasswordId}")
    fun deleteAccountPassword(
        @Header("Authorization") authHeader: String,
        @Path("CategoryName") categoryName: String,
        @Path("PasswordId") passwordId: Int
    ): Call<String>

}
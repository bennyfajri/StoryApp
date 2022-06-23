package com.drsync.storyapp.api

import com.drsync.storyapp.models.LoginRequest
import com.drsync.storyapp.models.RegisterRequest
import com.drsync.storyapp.models.ServerResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("register")
    suspend fun registerUser(
        @Body registerRequest: RegisterRequest
    ): ServerResponse

    @POST("login")
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ): ServerResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") header: String,
        @Query("page") page : Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ) : ServerResponse

    @Multipart
    @POST("stories")
    suspend fun tambahStory(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ) : ServerResponse

}
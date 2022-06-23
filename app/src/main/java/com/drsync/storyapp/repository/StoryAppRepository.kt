package com.drsync.storyapp.repository

import android.util.Log
import com.drsync.storyapp.api.ApiService
import com.drsync.storyapp.models.LoginRequest
import com.drsync.storyapp.models.RegisterRequest
import com.drsync.storyapp.models.ServerResponse
import com.drsync.storyapp.util.Constant.TAG
import com.drsync.storyapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class StoryAppRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun registerUser(registerRequest: RegisterRequest) = flow<Resource<ServerResponse>> {
        emit(Resource.loading())
        val response = apiService.registerUser(registerRequest)
        response.let {
            if (!it.error) emit(Resource.success(it))
            else emit(Resource.error(it.message))
        }
    }.catch {
        Log.d(TAG, "registerUser: ${it.message}")
        emit(Resource.error(it.message ?: ""))
    }.flowOn(Dispatchers.IO)


    fun loginUser(loginRequest: LoginRequest) = flow<Resource<ServerResponse>> {
        emit(Resource.loading())
        val response = apiService.loginUser(loginRequest)
        response.let {
            if (!it.error) emit(Resource.success(it))
            else emit(Resource.Error(it.message))
        }
    }.catch {
        Log.d(TAG, "loginUser: ${it.message}")
        emit(Resource.error(it.message ?: ""))
    }.flowOn(Dispatchers.IO)


    fun getAllStories(
        token: String,
        page: Int? = null,
        size: Int? = null,
        location: Int? = null
    ) = flow<Resource<ServerResponse>> {
        emit(Resource.loading())
        val response = apiService.getAllStories(token, page, size, location)
        response.let {
            if (!it.error) emit(Resource.success(it))
            else emit(Resource.error(it.message))
        }
    }.catch {
        Log.d(TAG, "getAllStories: ${it.message}")
        emit(Resource.error(it.message ?: ""))
    }.flowOn(Dispatchers.IO)

    fun tambahStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) = flow<Resource<ServerResponse>> {
        emit(Resource.loading())
        val response = apiService.tambahStory(token, file, description, lat, lon)
        response.let {
            if (!it.error) emit(Resource.success(it))
            else emit(Resource.error(it.message))
        }
    }.catch {
        Log.d(TAG, "getAllStories: ${it.message}")
        emit(Resource.error(it.message ?: ""))
    }.flowOn(Dispatchers.IO)
}

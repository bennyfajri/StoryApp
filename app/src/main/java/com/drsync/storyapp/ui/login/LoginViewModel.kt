package com.drsync.storyapp.ui.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drsync.storyapp.R
import com.drsync.storyapp.models.LoginRequest
import com.drsync.storyapp.models.User
import com.drsync.storyapp.models.UserPreference
import com.drsync.storyapp.repository.StoryAppRepository
import com.drsync.storyapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: StoryAppRepository,
    private val preference: UserPreference
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loginUser(
        context: Context,
        loginRequest: LoginRequest,
        onSuccess: (User) -> Unit
    ) = viewModelScope.launch {
        repository.loginUser(loginRequest).collect { response ->
            when (response) {
                is Resource.Loading -> {
                    _isLoading.value = true
                }
                is Resource.Success -> {
                    _isLoading.value = false

                    if (!response.data.error) {
                        preference.saveUser(response.data.loginResult as User)

                        Toast.makeText(
                            context,
                            context.getString(R.string.welcome, response.data.loginResult.name),
                            Toast.LENGTH_SHORT
                        ).show()

                        onSuccess(response.data.loginResult)
                    } else {
                        Toast.makeText(context, response.data.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    _isLoading.value = false
                    Toast.makeText(context, "Error: ${response.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getUser(
        user : (User) -> Unit
    ) = viewModelScope.launch {
        preference.getUser().collect{
            user(it)
        }
    }

}
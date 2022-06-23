package com.drsync.storyapp.ui.register

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drsync.storyapp.models.RegisterRequest
import com.drsync.storyapp.repository.StoryAppRepository
import com.drsync.storyapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: StoryAppRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun registerUser(
        context: Context,
        registerRequest: RegisterRequest,
        onSuccess: (Boolean) -> Unit
    ) = viewModelScope.launch {
        repository.registerUser(registerRequest).collect { response ->
            when (response) {
                is Resource.Loading -> {
                    _isLoading.value = true
                }
                is Resource.Success -> {
                    _isLoading.value = false
                    Toast.makeText(
                        context,
                        response.data.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    onSuccess(true)
                }
                is Resource.Error -> {
                    _isLoading.value = false
                    Toast.makeText(context, "Error: ${response.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
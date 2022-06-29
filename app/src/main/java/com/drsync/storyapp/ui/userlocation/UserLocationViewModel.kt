package com.drsync.storyapp.ui.userlocation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drsync.storyapp.models.Story
import com.drsync.storyapp.models.User
import com.drsync.storyapp.models.UserPreference
import com.drsync.storyapp.repository.StoryAppRepository
import com.drsync.storyapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLocationViewModel @Inject constructor(
    private val repository: StoryAppRepository,
    private val userPref: UserPreference
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading

    fun getStoryWithLocation(
        token: String,
        location : Int = 1,
        onSuccess: (List<Story>) -> Unit
    ) = viewModelScope.launch {
        repository.getStoryWithLocation(token, location).collect{ response ->
            when(response){
                is Resource.Loading -> {
                    _isLoading.value = true
                }
                is Resource.Success -> {
                    _isLoading.value = false
                    if(!response.data.error){
                        onSuccess(response.data.listStory as List<Story>)
                    }
                }
                is Resource.Error -> {
                    _isLoading.value = false
                }
            }
        }
    }

    fun getUser(
        user: (User) -> Unit
    ) = viewModelScope.launch {
        userPref.getUser().collect{
            user(it)
        }
    }
}
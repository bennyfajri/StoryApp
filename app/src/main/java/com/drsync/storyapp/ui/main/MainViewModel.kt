package com.drsync.storyapp.ui.main

import androidx.lifecycle.LiveData
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
class MainViewModel @Inject constructor(
    private val repository: StoryAppRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    companion object {
        var listStory = ArrayList<Story>()
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    fun getStories(
        token: String,
        page: Int?= null,
        size: Int?= null,
        location: Int?= null,
        onSuccess: (List<Story>) -> Unit
    ) = viewModelScope.launch {
        repository.getAllStories(token, page, size, location).collect{ response ->
            when(response){
                is Resource.Loading -> {
                    _isLoading.value = true
                }
                is Resource.Success -> {
                    _isLoading.value = false
                    if(!response.data.error){
                        onSuccess(response.data.listStory as List<Story>)
                        listStory = response.data.listStory as ArrayList<Story>
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
        userPreference.getUser().collect{
            user(it)
        }
    }

    fun logout(user: User) = viewModelScope.launch {
        userPreference.deleteUser(user)
    }


}
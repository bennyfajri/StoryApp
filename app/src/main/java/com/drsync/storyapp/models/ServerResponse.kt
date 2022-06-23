package com.drsync.storyapp.models

import com.google.gson.annotations.SerializedName

data class ServerResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("loginResult")
    val loginResult: User? = null,

    @field:SerializedName("listStory")
    val listStory : List<Story>? = null
)

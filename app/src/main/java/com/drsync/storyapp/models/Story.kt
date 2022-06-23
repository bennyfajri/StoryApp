package com.drsync.storyapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    @field:SerializedName("id")
    val id: String? = null,
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("description")
    val description: String? = null,
    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,
    @field:SerializedName("createdAt")
    val createdAt: String? = null,
    @field:SerializedName("lat")
    val lat: String? = null,
    @field:SerializedName("lon")
    val lon: String? = null
) : Parcelable

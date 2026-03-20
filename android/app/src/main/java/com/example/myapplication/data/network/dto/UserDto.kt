package com.example.myapplication.data.network.dto

import com.example.myapplication.domain.User
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("specialization") val specialization: String,
    @SerializedName("licenceNumber") val licenceNumber: String,
    @SerializedName("role") val role: String
)
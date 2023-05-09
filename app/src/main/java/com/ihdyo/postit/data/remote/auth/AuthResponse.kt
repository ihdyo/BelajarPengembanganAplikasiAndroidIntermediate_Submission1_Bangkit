package com.ihdyo.postit.data.remote.auth

import com.ihdyo.postit.data.model.User
import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("loginResult")
    val loginResult: User
)
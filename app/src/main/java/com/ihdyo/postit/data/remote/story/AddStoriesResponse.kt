package com.ihdyo.postit.data.remote.story

import com.google.gson.annotations.SerializedName

data class AddStoriesResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)

package com.greedygames.geticons.data.models

import com.google.gson.annotations.SerializedName

class License(
    @SerializedName("license_id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String?
)
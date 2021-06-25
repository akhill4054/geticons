package com.greedygames.geticons.data.models

import com.google.gson.annotations.SerializedName

class License(
    @SerializedName("name")
    val name: String,
    @SerializedName("scope")
    val scope: String,
    @SerializedName("license_id")
    val licenseId: String,
    @SerializedName("url")
    val url: String
)
package com.greedygames.geticons.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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
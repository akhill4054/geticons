package com.greedygames.geticons.data.models

import com.google.gson.annotations.SerializedName

data class Icon(
    @SerializedName("icon_id")
    val id: String,
    @SerializedName("size")
    val size: Int,
    @SerializedName("categories")
    val categories: List<Category>,
    @SerializedName("type")
    val type: String,
    val isPremium: Boolean,
)

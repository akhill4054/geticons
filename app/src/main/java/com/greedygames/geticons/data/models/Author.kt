package com.greedygames.geticons.data.models

import com.google.gson.annotations.SerializedName

class Author(
    @SerializedName("company")
    val company: String,
    @SerializedName("prices")
    val username: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("is_designer")
    val isDesigner: Boolean,
    @SerializedName("icon_sets_count")
    val iconSetsCount: Int
)
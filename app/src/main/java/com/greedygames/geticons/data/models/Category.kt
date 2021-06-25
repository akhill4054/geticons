package com.greedygames.geticons.data.models

import com.google.gson.annotations.SerializedName

class Category(
    @SerializedName("name")
    val name: String,
    @SerializedName("identifier")
    val identifier: String
)
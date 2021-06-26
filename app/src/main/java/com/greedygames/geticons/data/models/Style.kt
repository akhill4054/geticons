package com.greedygames.geticons.data.models

import com.google.gson.annotations.SerializedName

class Style(
    @SerializedName("name")
    val name: String,
    @SerializedName("identifier")
    val identifier: String
)
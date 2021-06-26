package com.greedygames.geticons.data.net.models

import com.google.gson.annotations.SerializedName
import com.greedygames.geticons.data.models.Style

class StylesResponse(
    @SerializedName("styles")
    val styles: List<Style>
)
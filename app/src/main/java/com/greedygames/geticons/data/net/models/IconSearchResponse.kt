package com.greedygames.geticons.data.net.models

import com.google.gson.annotations.SerializedName
import com.greedygames.geticons.data.models.Icon

class IconSearchResponse(
    @SerializedName("icons")
    val icons: List<Icon>
)
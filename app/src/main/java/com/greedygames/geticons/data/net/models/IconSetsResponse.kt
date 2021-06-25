package com.greedygames.geticons.data.net.models

import com.google.gson.annotations.SerializedName
import com.greedygames.geticons.data.models.IconSet

class IconSetsResponse(
    @SerializedName("iconsets")
    val iconSets: List<IconSet>
)
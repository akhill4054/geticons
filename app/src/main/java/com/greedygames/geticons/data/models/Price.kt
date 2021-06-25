package com.greedygames.geticons.data.models

import com.google.gson.annotations.SerializedName

class Price(
    @SerializedName("price")
    val price: Int,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("license")
    val license: License
)
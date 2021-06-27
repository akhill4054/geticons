package com.greedygames.geticons.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class Price(
    @SerializedName("price")
    val price: Float,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("license")
    val license: License
)
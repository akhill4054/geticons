package com.greedygames.geticons.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable
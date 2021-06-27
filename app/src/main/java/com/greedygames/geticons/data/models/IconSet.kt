package com.greedygames.geticons.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.greedygames.geticons.NOT_AVAILABLE
import kotlinx.parcelize.Parcelize
import java.util.*

data class IconSet(
    @SerializedName("iconset_id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("author")
    val author: Author,
    @SerializedName("type")
    val type: String,
    @SerializedName("is_premium")
    val isPremium: Boolean,
    @SerializedName("prices")
    val prices: List<Price>?,
    @SerializedName("license")
    val license: License?
) {
    fun getFormattedPrice(): String {
        return if (isPremium && prices?.isNotEmpty() == true) {
            val price = prices[0]
            if (price.currency == "USD") {
                "$${price.price}"
            } else {
                "${price.currency} ${price.price}"
            }
        } else {
            "$0"
        }
    }

    fun getFormattedType(): String = type.replaceFirstChar {
        it.uppercaseChar()
    }

    fun getFormattedLicense(): String {
        return when {
            // If the is premium and prices is not
            // empty then license is will be inside
            // the Price.
            prices?.isNotEmpty() == true -> {
                val license = prices[0].license
                license.name
            }
            // Otherwise, it will be inside the IconSet.
            license != null -> {
                license.name
            }
            else -> {
                NOT_AVAILABLE
            }
        }
    }
}
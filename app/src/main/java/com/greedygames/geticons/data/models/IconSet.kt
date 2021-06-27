package com.greedygames.geticons.data.models

import com.google.gson.annotations.SerializedName
import com.greedygames.geticons.NOT_APPLICABLE
import com.greedygames.geticons.NOT_AVAILABLE

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
    val license: License?,
    @SerializedName("readme")
    val readme: String?,
    @SerializedName("website_url")
    val websiteUrl: String?,
    @SerializedName("icons_count")
    val iconsCount: Int
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
        return getFinalLicense()?.name ?: NOT_APPLICABLE
    }

    fun getFinalLicense(): License? {
        // If the is premium and prices is not
        // empty then license is will be inside
        // the Price.
        return if (prices?.isNotEmpty() == true) {
            prices[0].license
        }
        // Otherwise, it will be inside the IconSet.
        else {
            license
        }
    }

    fun getFormattedReadme(): String {
        return if (readme.isNullOrEmpty()) {
            NOT_AVAILABLE
        } else {
            readme
        }
    }

    fun getFormattedWebsiteUrl(): String {
        return when {
            !websiteUrl.isNullOrEmpty() -> {
                websiteUrl
            }
            !author.website.isNullOrEmpty() -> {
                author.website
            }
            else -> {
                "Website: $NOT_APPLICABLE"
            }
        }
    }
}
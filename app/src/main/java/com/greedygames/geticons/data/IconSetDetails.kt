package com.greedygames.geticons.data

import com.google.gson.annotations.SerializedName
import com.greedygames.geticons.NOT_APPLICABLE
import com.greedygames.geticons.data.models.Author
import com.greedygames.geticons.data.models.License
import com.greedygames.geticons.data.models.Price

class IconSetDetails(
    @SerializedName("iconset_id")
    val id: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("readme")
    val readme: String?,
    @SerializedName("is_premium")
    val isPremium: Boolean,
    @SerializedName("website_url")
    val websiteUrl: String?,
    @SerializedName("author")
    val author: Author,
    @SerializedName("prices")
    val prices: List<Price>?,
    @SerializedName("license")
    val license: License?
) {
    fun getFormattedReadme(): String {
        return if (readme.isNullOrEmpty()) {
            NOT_APPLICABLE
        } else {
            readme
        }
    }

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

    fun getFormattedType(): String = type.replaceFirstChar { it.uppercase() }

    fun getFormattedWebsite(): String {
        return if (websiteUrl.isNullOrEmpty()) {
            NOT_APPLICABLE
        } else {
            websiteUrl
        }
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
}
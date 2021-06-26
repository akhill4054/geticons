package com.greedygames.geticons.data

import com.google.gson.annotations.SerializedName
import com.greedygames.geticons.NOT_APPLICABLE
import com.greedygames.geticons.data.models.License

class IconSetDetails(
    @SerializedName("license")
    val license: License,
    @SerializedName("readme")
    val readme: String?,
    @SerializedName("website_url")
    val websiteUrl: String?,
) {
    fun getFormattedReadme(): String {
        return if (readme.isNullOrEmpty()) {
            NOT_APPLICABLE
        } else {
            readme
        }
    }

    fun getFormattedWebsite(): String {
        return if (websiteUrl.isNullOrEmpty()) {
            NOT_APPLICABLE
        } else {
            websiteUrl
        }
    }
}
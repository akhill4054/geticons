package com.greedygames.geticons.data.models

import com.google.gson.annotations.SerializedName
import com.greedygames.geticons.NOT_APPLICABLE

/**
 * @see Author may represent either an author or
 * a user, it'll depend on out of userId and authorId
 * which one is not null.
 * */
class Author(
    @SerializedName("user_id")
    val userId: Int? = null,
    @SerializedName("author_id")
    val authorId: Int? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("website")
    val website: String?,
    @SerializedName("iconsets_count")
    val iconSetsCount: Int
) {

    fun getFormattedWebsiteUrl(): String {
        return if (!website.isNullOrEmpty()) {
            website
        } else {
            "Website: $NOT_APPLICABLE"
        }
    }
}
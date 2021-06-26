package com.greedygames.geticons.data.models

import com.google.gson.annotations.SerializedName
import com.greedygames.geticons.IDEAL_ICON_PREVIEW_SIZE
import com.greedygames.geticons.data.models.Icon.RasterSize
import com.greedygames.geticons.data.models.Icon.VectorSize

/**
 *
 * @param rasterSizes, Contains the download URLs and preview URLs for the
 * icon in different sizes if the icon is raster format.
 * @see RasterSize
 * @param vectorSizes, Contains the download URLs for the icon if the icon
 * is vector format.
 * @see VectorSize
 * */
data class Icon(
    @SerializedName("icon_id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("is_premium")
    val isPremium: Boolean,
    @SerializedName("prices")
    val prices: List<Price>?,
    @SerializedName("raster_sizes")
    val rasterSizes: List<RasterSize>?,
    @SerializedName("vector_sizes")
    val vectorSizes: List<VectorSize>?
) {
    // Icon preview URL
    var previewUrl: String? = null

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

    fun initPreviewUrl() {
        if (rasterSizes != null) {
            // Getting ideal icon preview
            for (i in rasterSizes.indices) {
                val rs = rasterSizes[i]

                if (rs.size >= IDEAL_ICON_PREVIEW_SIZE || i == rasterSizes.size - 1) {
                    this.previewUrl = rs.formats[0].previewUrl
                    break
                }
            }
        }
    }

    fun getDownloadUrl(): String? {
        return null
    }

    class RasterSize(
        @SerializedName("size_height")
        val height: Int,
        @SerializedName("size_width")
        val width: Int,
        @SerializedName("formats")
        val formats: List<Format>,
        val size: Int
    )

    class VectorSize(
        @SerializedName("size_height")
        val height: Int,
        @SerializedName("size_width")
        val width: Int,
        @SerializedName("formats")
        val formats: List<Format>
    )

    class Format(
        @SerializedName("preview_url")
        val previewUrl: String,
        @SerializedName("format")
        val format: String,
        @SerializedName("download_url")
        val downloadUrl: String,
    )
}
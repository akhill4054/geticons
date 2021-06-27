package com.greedygames.geticons.repositories

import android.app.Application
import com.greedygames.geticons.PAGINATION_ITEM_COUNT
import com.greedygames.geticons.data.models.Category
import com.greedygames.geticons.data.models.Style
import com.greedygames.geticons.data.net.ApiClient
import com.greedygames.geticons.data.net.models.CategoriesResponse
import com.greedygames.geticons.data.net.models.IconListResponse
import com.greedygames.geticons.data.net.models.StylesResponse

class IconRepository private constructor(application: Application) {

    private val apiInterface = ApiClient.getInstance(application).apiInterface

    suspend fun searchIcons(
        offset: Int = 0,
        searchQuery: String,
        searchFilter: IconSearchFilter?
    ): IconListResponse {
        // Additional query params for better search.
        val params: HashMap<String, String> = (searchFilter?.run {
            val map = HashMap<String, String>()

            premium?.let {
                map.put("premium", if (premium) "true" else "false")
            }
            license?.let {
                if (it == IconSearchFilter.LICENSES_COMMERCIAL) {
                    map["license"] = "commercial"
                } else if (it == IconSearchFilter.LICENCES_NO_ATTRIBUTION) {
                    map["license"] = "nonattribution"
                }
            }
            category?.let {
                map["category"] = it.identifier
            }
            style?.let {
                map["style"] = it.identifier
            }

            return@run map
        }) ?: HashMap()

        return apiInterface.searchIcons(
            offset,
            PAGINATION_ITEM_COUNT,
            searchQuery,
            params
        ).apply {
            for (icon in icons) {
                icon.initPreviewUrl()
            }
        }
    }

    suspend fun getCategories(): CategoriesResponse =
        apiInterface.getCategories()

    suspend fun getStyles(): StylesResponse =
        apiInterface.getStyles()

    /**
     * Class to define search filters.
     * @param license is specified by,
     * @see LICENSES_ALL
     * @see LICENSES_COMMERCIAL
     * @see LICENCES_NO_ATTRIBUTION
     * */
    data class IconSearchFilter(
        val premium: Boolean? = null,
        val license: Int? = LICENSES_ALL,
        val category: Category? = null,
        val style: Style? = null
    ) {
        companion object {
            const val LICENSES_ALL = 0
            const val LICENSES_COMMERCIAL = 1
            const val LICENCES_NO_ATTRIBUTION = 2
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: IconRepository? = null

        fun getInstance(application: Application): IconRepository {
            // To reduce number of reads on volatile field.
            var localRef = INSTANCE

            return localRef ?: synchronized(IconRepository::class.java) {
                // To reduce number of reads on volatile field.
                localRef = INSTANCE

                // As static field may have been initialized before
                // the lock was acquired.
                return localRef ?: IconRepository(application).also {
                    // Init static field.
                    INSTANCE = it
                }
            }
        }
    }
}
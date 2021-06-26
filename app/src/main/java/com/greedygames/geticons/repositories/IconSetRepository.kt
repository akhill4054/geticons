package com.greedygames.geticons.repositories

import android.app.Application
import com.greedygames.geticons.PAGINATION_ITEM_COUNT
import com.greedygames.geticons.data.IconSetDetails
import com.greedygames.geticons.data.models.IconSet
import com.greedygames.geticons.data.net.ApiClient
import com.greedygames.geticons.data.net.ApiInterface
import com.greedygames.geticons.data.net.models.IconSetsResponse

class IconSetRepository private constructor(application: Application) {

    private val apiInterface: ApiInterface

    init {
        val apiClient = ApiClient.getInstance(application)
        apiInterface = apiClient.apiInterface
    }

    suspend fun getIconSets(after: Int?): IconSetsResponse =
        apiInterface.getIconSets(after = after, count = PAGINATION_ITEM_COUNT)

    suspend fun getIconSetDetails(iconSet: IconSet): IconSetDetails =
        apiInterface.getIconSetDetails(iconSet.id)

    companion object {
        @Volatile
        private var INSTANCE: IconSetRepository? = null

        fun getInstance(application: Application): IconSetRepository {
            // To reduce number of reads on volatile field.
            var localRef = INSTANCE

            return localRef ?: synchronized(IconSetRepository::class.java) {
                // To reduce number of reads on volatile field.
                localRef = INSTANCE

                // As static field may have been initialized before
                // the lock was acquired.
                return localRef ?: IconSetRepository(application).also {
                    // Init static field.
                    INSTANCE = it
                }
            }
        }
    }
}
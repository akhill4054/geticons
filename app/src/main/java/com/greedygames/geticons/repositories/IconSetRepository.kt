package com.greedygames.geticons.repositories

import android.app.Application
import com.greedygames.geticons.data.net.ApiClient
import com.greedygames.geticons.data.net.ApiInterface
import com.greedygames.geticons.data.net.models.IconSetsResponse

class IconSetRepository private constructor(application: Application) {

    private val apiInterface: ApiInterface

    init {
        val apiClient = ApiClient.getInstance(application)
        apiInterface = apiClient.apiInterface
    }

    /**
     * @param after, Icon set ID of the last icon set received.
     * If empty, the count most recently published icon sets are returned.
     * */
    suspend fun getIconSets(after: Int?): IconSetsResponse =
        apiInterface.getIconSets(after = after)

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
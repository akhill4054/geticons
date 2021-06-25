package com.greedygames.geticons.data.net

import com.greedygames.geticons.PAGINATION_ITEM_COUNT
import com.greedygames.geticons.data.net.models.IconSetsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/v4/iconsets?count=$PAGINATION_ITEM_COUNT")
    suspend fun getIconSets(
        @Query("after") after: Int? = null
    ): IconSetsResponse
}
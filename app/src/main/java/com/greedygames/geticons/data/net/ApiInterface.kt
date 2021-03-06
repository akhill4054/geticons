package com.greedygames.geticons.data.net

import com.greedygames.geticons.data.models.Author
import com.greedygames.geticons.data.models.Icon
import com.greedygames.geticons.data.models.IconSet
import com.greedygames.geticons.data.net.models.CategoriesResponse
import com.greedygames.geticons.data.net.models.IconListResponse
import com.greedygames.geticons.data.net.models.IconSetsResponse
import com.greedygames.geticons.data.net.models.StylesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiInterface {
    // IconSet
    /**
     * @param after, Icon set ID of the last icon set received.
     * If empty, the count most recently published icon sets are returned.
     * */
    @GET("/v4/iconsets")
    suspend fun getIconSets(
        @Query("after") after: Int?,
        @Query("count") count: Int
    ): IconSetsResponse

    @GET("/v4/iconsets/{iconset_id}")
    suspend fun getIconSetDetails(@Path("iconset_id") iconSetId: Int): Response<IconSet>

    @GET("/v4/iconsets/{iconset_id}/icons")
    suspend fun getIconSetIcons(@Path("iconset_id") iconSetId: Int): IconListResponse

    @GET("/v4/icons/search")
    suspend fun getIcons(
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): IconListResponse

    // Icon
    /**
     * @param params, available extra query params to pass:
     * premium: boolean, true -> premium, false -> free.
     * license: string, none/empty, commercial, nonattribution.
     * category: string, Filter icons based on category identifier.
     * vector: string, 0/false -> only raster, 1/true -> only vector.
     * style: string.
     * */
    @GET("/v4/icons/search")
    suspend fun searchIcons(
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("query") query: String,
        @QueryMap params: Map<String, String>
    ): IconListResponse

    @GET("/v4/icons/{icon_id}")
    suspend fun getIconDetails(@Path("icon_id") iconId: Int): Icon

    @GET("/v4/categories")
    suspend fun getCategories(): CategoriesResponse

    @GET("/v4/styles")
    suspend fun getStyles(): StylesResponse

    // Author
    @GET("/v4/authors/{author_id}")
    suspend fun getAuthorDetails(@Path("author_id") authorId: Int): Author

    @GET("/v4/authors/{author_id}/iconsets")
    suspend fun getAuthorIconSets(
        @Path("author_id") authorId: Int,
        @Query("count") count: Int,
        @Query("after") after: Int?
    ): IconSetsResponse

    // User
    @GET("/v4/users/{user_id}")
    suspend fun getUserDetails(@Path("user_id") userId: Int): Author

    @GET("/v4/users/{user_id}/iconsets")
    suspend fun getUserIconSets(
        @Path("user_id") userId: Int,
        @Query("count") count: Int,
        @Query("after") after: Int?
    ): IconSetsResponse
}
package com.greedygames.geticons.data.net.models

import com.google.gson.annotations.SerializedName
import com.greedygames.geticons.data.models.Category

class CategoriesResponse(
    @SerializedName("categories")
    val categories: List<Category>
)
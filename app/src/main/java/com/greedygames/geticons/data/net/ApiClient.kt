package com.greedygames.geticons.data.net

import android.app.Application
import com.greedygames.geticons.BASE_URL
import com.greedygames.geticons.CONNECTION_TIMEOUT_IN_SECONDS
import com.greedygames.geticons.R
import com.greedygames.geticons.REQUEST_TIMEOUT_IN_SECONDS
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient private constructor(application: Application) {

    val apiInterface: ApiInterface

    init {
        // Building and setting-up http interceptor
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.readTimeout(
            REQUEST_TIMEOUT_IN_SECONDS.toLong(),
            TimeUnit.SECONDS
        )
        httpClientBuilder.connectTimeout(
            CONNECTION_TIMEOUT_IN_SECONDS.toLong(),
            TimeUnit.SECONDS
        )
        httpClientBuilder.addInterceptor { chain ->
            val request = chain.request().newBuilder()

            // Add global headers here
            request.addHeader("Accept", "application/json")
            request.addHeader(
                "Authorization",
                "Bearer ${application.getString(R.string.api_key)}"
            )

            chain.proceed(request.build())
        }
        val httpClient = httpClientBuilder.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiInterface = retrofit.create(ApiInterface::class.java)
    }

    companion object {
        @Volatile
        private var INSTANCE: ApiClient? = null

        fun getInstance(application: Application): ApiClient {
            // To reduce number of reads on volatile field.
            var localRef = INSTANCE

            return localRef ?: synchronized(ApiClient::class.java) {
                // To reduce number of reads on volatile field.
                localRef = INSTANCE

                // As static field may have been initialized before
                // the lock was acquired.
                return localRef ?: ApiClient(application).also {
                    // Init static field.
                    INSTANCE = it
                }
            }
        }
    }
}
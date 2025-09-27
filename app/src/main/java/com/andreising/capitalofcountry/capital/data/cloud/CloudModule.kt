package com.andreising.capitalofcountry.capital.data.cloud

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface CloudModule {
    fun <T> service(clasz: Class<T>): T

    class Base(private val isDebug: Boolean) : CloudModule {
        override fun <T> service(clasz: Class<T>): T {
            val interceptor = HttpLoggingInterceptor().apply {
                setLevel(
                    if (isDebug)
                        HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                )
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(clasz)
        }
    }

    companion object {
        const val BASE_URL = "https://restcountries.com/v3.1/"
    }
}
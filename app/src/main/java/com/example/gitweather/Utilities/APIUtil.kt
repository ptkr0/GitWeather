package com.example.gitweather.Utilities

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIUtil {

    private var retrofit: Retrofit? = null
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    fun getIAPI(): IAPI {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(IAPI::class.java) // !! will throw an exception if retrofit is null
    }
}
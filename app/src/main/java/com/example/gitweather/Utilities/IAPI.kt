package com.example.gitweather.Utilities

import com.example.gitweather.POJO.Model
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IAPI {

    @GET("weather")
    fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") api_key: String,
        @Query("units") units: String = "metric",
    ): Call<Model>

    @GET("weather")
    fun getCityWeather(
        @Query("q") city: String,
        @Query("appid") api_key: String,
        @Query("units") units: String = "metric",
    ): Call<Model>
}
package com.example.gitweather.POJO

import com.google.gson.annotations.SerializedName

data class Model(
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("main") val main: Main,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("timezone") val timezone: Int,
    @SerializedName("name") val name: String,
)

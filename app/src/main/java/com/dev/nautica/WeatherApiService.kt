package com.dev.nautica

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class ApiResponse(
    val response: WeatherResponse
)

data class WeatherResponse(
    val display: DisplayData,
    val suitability_percentage: Int
)

data class DisplayData(
    val location: String,
    val temp: Double,
    val waterTemp: Double,
    val avgHumidity: Double,
    val windSpeed: Double,
    val cloud: Double,
    val uv: Double,
    val windDeg: Double,
    val sunrise: String,
    val sunset: String,
    val condition: ConditionData
)

data class ConditionData(
    val text: String,
    val icon: String
)

interface WeatherApiService {
    @GET("api/predict")
    fun getWeatherData(
        @Query("la") latitude: Double,
        @Query("lo") longitude: Double,
        @Query("time") time: Int
    ): Call<ApiResponse>
}

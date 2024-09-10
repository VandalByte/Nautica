package com.dev.nautica

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://a24a-122-187-117-178.ngrok-free.app/"
//    private const val BASE_URL = "http://localhost:5000/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherApiService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}

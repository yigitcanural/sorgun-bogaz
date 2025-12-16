package com.deniz.durumu.api

import com.deniz.durumu.data.MarineResponse
import com.deniz.durumu.data.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {
    
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m,wind_direction_10m,wind_gusts_10m,uv_index",
        @Query("hourly") hourly: String = "temperature_2m,weather_code",
        @Query("timezone") timezone: String = "Europe/Istanbul",
        @Query("forecast_days") forecastDays: Int = 1
    ): WeatherResponse
}

interface OpenMeteoMarineApi {
    
    @GET("v1/marine")
    suspend fun getMarine(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "wave_height,wave_direction,wave_period,swell_wave_height",
        @Query("hourly") hourly: String = "wave_height,sea_surface_temperature",
        @Query("timezone") timezone: String = "Europe/Istanbul",
        @Query("forecast_days") forecastDays: Int = 1
    ): MarineResponse
}

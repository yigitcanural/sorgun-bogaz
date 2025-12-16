package com.deniz.durumu.data

// Weather API Response
data class WeatherResponse(
    val current: CurrentWeather?,
    val hourly: HourlyWeather?
)

data class CurrentWeather(
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val apparent_temperature: Double,
    val weather_code: Int,
    val wind_speed_10m: Double,
    val wind_direction_10m: Int,
    val wind_gusts_10m: Double,
    val uv_index: Double?
)

data class HourlyWeather(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val weather_code: List<Int>
)

// Marine API Response
data class MarineResponse(
    val current: CurrentMarine?,
    val hourly: HourlyMarine?
)

data class CurrentMarine(
    val wave_height: Double,
    val wave_direction: Int,
    val wave_period: Double,
    val swell_wave_height: Double?,
    val ocean_current_velocity: Double?,
    val ocean_current_direction: Int?
)

data class HourlyMarine(
    val time: List<String>,
    val wave_height: List<Double>,
    val sea_surface_temperature: List<Double>?
)

// Combined Sea Conditions Data
data class SeaConditions(
    val airTemperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val windGusts: Double,
    val weatherCode: Int,
    val waveHeight: Double,
    val waveDirection: Int,
    val wavePeriod: Double,
    val seaTemperature: Double,
    val uvIndex: Double,
    val swimmingQuality: SwimmingQuality,
    val swimmingScore: Int, // 0-100 percentage
    val lastUpdated: String
)

// Pure black/white - no colors
enum class SwimmingQuality(val label: String, val description: String) {
    EXCELLENT("Mükemmel", "Deniz koşulları mükemmel! Yüzmenin tadını çıkarın."),
    GOOD("İyi", "Denize girmek için uygun. Hafif dalgalara dikkat edin."),
    MODERATE("Orta", "Dikkatli olunmalı. Deneyimli yüzücüler için uygundur."),
    POOR("Uygun Değil", "Denize girmeyin! Koşullar uygun değil.")
}

// Contextual description functions
object ConditionDescriptions {
    
    fun getAirTempDescription(temp: Double): String {
        return when {
            temp >= 35 -> "Çok sıcak"
            temp >= 30 -> "Sıcak"
            temp >= 25 -> "Ilık"
            temp >= 20 -> "Serin"
            temp >= 15 -> "Soğuk"
            else -> "Çok soğuk"
        }
    }
    
    fun getSeaTempDescription(temp: Double): String {
        return when {
            temp >= 26 -> "Ilık, yüzmeye ideal"
            temp >= 23 -> "Yüzmeye müsait"
            temp >= 20 -> "Serin ama yüzülebilir"
            temp >= 17 -> "Soğuk"
            else -> "Çok soğuk"
        }
    }
    
    fun getWaveDescription(height: Double): String {
        return when {
            height < 0.2 -> "Sakin"
            height < 0.5 -> "Hafif dalgalı"
            height < 1.0 -> "Dalgalı"
            height < 1.5 -> "Yüksek dalgalı"
            else -> "Çok dalgalı"
        }
    }
    
    fun getWindDescription(speed: Double): String {
        return when {
            speed < 10 -> "Sakin"
            speed < 20 -> "Hafif rüzgarlı"
            speed < 30 -> "Rüzgarlı"
            speed < 45 -> "Kuvvetli rüzgar"
            else -> "Çok kuvvetli rüzgar"
        }
    }
    
    fun getUvDescription(uv: Double): String {
        return when {
            uv < 3 -> "Düşük"
            uv < 6 -> "Orta"
            uv < 8 -> "Yüksek"
            uv < 11 -> "Çok yüksek"
            else -> "Aşırı yüksek"
        }
    }
}

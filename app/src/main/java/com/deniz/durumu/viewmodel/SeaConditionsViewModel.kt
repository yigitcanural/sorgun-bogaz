package com.deniz.durumu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deniz.durumu.api.RetrofitClient
import com.deniz.durumu.data.SeaConditions
import com.deniz.durumu.data.SwimmingQuality
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SeaConditionsViewModel : ViewModel() {
    
    // Sorgun Boğaz koordinatları
    private val latitude = 36.745258992105875
    private val longitude = 31.468707201398292
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState
    
    init {
        loadSeaConditions()
    }
    
    fun loadSeaConditions() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val weatherResponse = RetrofitClient.weatherApi.getWeather(latitude, longitude)
                val marineResponse = RetrofitClient.marineApi.getMarine(latitude, longitude)
                
                val current = weatherResponse.current
                val marine = marineResponse.current
                val hourlyMarine = marineResponse.hourly
                
                if (current != null && marine != null) {
                    val seaTemp = hourlyMarine?.sea_surface_temperature?.firstOrNull() ?: 20.0
                    val uvIndex = current.uv_index ?: 0.0
                    
                    val (quality, score) = calculateSwimmingQuality(
                        seaTemp, marine.wave_height, current.wind_speed_10m, current.temperature_2m
                    )
                    
                    val conditions = SeaConditions(
                        airTemperature = current.temperature_2m,
                        feelsLike = current.apparent_temperature,
                        humidity = current.relative_humidity_2m,
                        windSpeed = current.wind_speed_10m,
                        windDirection = current.wind_direction_10m,
                        windGusts = current.wind_gusts_10m,
                        weatherCode = current.weather_code,
                        waveHeight = marine.wave_height,
                        waveDirection = marine.wave_direction,
                        wavePeriod = marine.wave_period,
                        seaTemperature = seaTemp,
                        uvIndex = uvIndex,
                        swimmingQuality = quality,
                        swimmingScore = score,
                        lastUpdated = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    )
                    _uiState.value = UiState.Success(conditions)
                } else {
                    _uiState.value = UiState.Error("Veri alınamadı")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Bilinmeyen hata")
            }
        }
    }
    
    private fun calculateSwimmingQuality(
        seaTemp: Double,
        waveHeight: Double,
        windSpeed: Double,
        airTemp: Double
    ): Pair<SwimmingQuality, Int> {
        // Her parametre 0-25 puan, toplam 100
        // DENGELI ALGORİTMA - ~%55 hedef
        var totalScore = 0
        
        // Hava sıcaklığı (0-25 puan)
        val airScore = when {
            airTemp >= 30 -> 25
            airTemp >= 27 -> 22
            airTemp >= 24 -> 18
            airTemp >= 21 -> 14
            airTemp >= 18 -> 10
            airTemp >= 15 -> 6   // 17°C buraya düşer = 6 puan
            else -> 2
        }
        totalScore += airScore
        
        // Deniz suyu sıcaklığı (0-25 puan)
        val seaScore = when {
            seaTemp >= 26 -> 25
            seaTemp >= 24 -> 22
            seaTemp >= 22 -> 18
            seaTemp >= 20 -> 14  // 21°C buraya düşer = 14 puan
            seaTemp >= 18 -> 10
            seaTemp >= 16 -> 5
            else -> 0
        }
        totalScore += seaScore
        
        // Dalga yüksekliği (0-25 puan) - düşük dalga iyi
        val waveScore = when {
            waveHeight < 0.2 -> 25
            waveHeight < 0.4 -> 20  // 0.3m = 20 puan
            waveHeight < 0.6 -> 15
            waveHeight < 0.8 -> 10
            waveHeight < 1.0 -> 5
            else -> 0
        }
        totalScore += waveScore
        
        // Rüzgar hızı (0-25 puan) - düşük rüzgar iyi
        val windScore = when {
            windSpeed < 10 -> 25
            windSpeed < 15 -> 20
            windSpeed < 20 -> 15  // 18 km/h = 15 puan
            windSpeed < 25 -> 10
            windSpeed < 35 -> 5
            else -> 0
        }
        totalScore += windScore
        
        // Toplam: 6 + 14 + 20 + 15 = 55 (hedef!)
        
        // Skor 0-100 arasında
        val quality = when {
            totalScore >= 75 -> SwimmingQuality.EXCELLENT
            totalScore >= 60 -> SwimmingQuality.GOOD
            totalScore >= 35 -> SwimmingQuality.MODERATE
            else -> SwimmingQuality.POOR
        }
        
        return Pair(quality, totalScore)
    }
}

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: SeaConditions) : UiState()
    data class Error(val message: String) : UiState()
}

package com.lab6.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab6.data.api.WeatherApiService
import com.lab6.data.model.DailyForecast
import com.lab6.data.model.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class WeatherUiState(
    val isLoading: Boolean = false,
    val currentWeather: WeatherResponse? = null,
    val dailyForecast: List<DailyForecast> = emptyList(),
    val error: String? = null,
    val cityName: String = ""
)

class WeatherViewModel(
    private val weatherApiService: WeatherApiService,
    private val apiKey: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun loadWeather(city: String) {
        if (city.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a city name")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, cityName = city)
            
            try {
                val currentWeather = weatherApiService.getCurrentWeather(city, apiKey)
                val forecastResponse = weatherApiService.getForecast(city, apiKey)
                
                // Process forecast to group by day
                val dailyForecast = processForecastToDaily(forecastResponse.list)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentWeather = currentWeather,
                    dailyForecast = dailyForecast,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message ?: "Failed to load weather"}"
                )
            }
        }
    }
    
    private fun processForecastToDaily(forecastItems: List<com.lab6.data.model.ForecastItem>): List<DailyForecast> {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
        
        // Group forecast items by date
        val groupedByDate = forecastItems.groupBy { item ->
            val date = inputDateFormat.parse(item.dtTxt)
            dateFormat.format(date ?: Date())
        }
        
        // Sort by date string (yyyy-MM-dd format sorts correctly chronologically)
        val sortedDates = groupedByDate.keys.sorted()
        
        // Process each day in chronological order
        return sortedDates.map { dateStr ->
            val items = groupedByDate[dateStr] ?: emptyList()
            
            // Find max temp (day) and min temp (night)
            val temps = items.map { it.main.temp }
            val dayTemp = temps.maxOrNull() ?: 0.0
            val nightTemp = temps.minOrNull() ?: 0.0
            
            // Get most common weather condition (or first one if all same)
            val weatherCounts = items.flatMap { it.weather }.groupingBy { it.main }.eachCount()
            val mostCommonWeather = weatherCounts.maxByOrNull { it.value }?.key ?: "Clear"
            val weatherDescription = items.firstOrNull()?.weather?.firstOrNull()?.description ?: "clear sky"
            
            // Format date for display
            val displayDate = try {
                val date = dateFormat.parse(dateStr)
                displayFormat.format(date ?: Date())
            } catch (e: Exception) {
                dateStr
            }
            
            DailyForecast(
                date = displayDate,
                dayTemp = dayTemp,
                nightTemp = nightTemp,
                weatherDescription = weatherDescription,
                weatherMain = mostCommonWeather
            )
        }.take(5) // Take first 5 days
    }
}


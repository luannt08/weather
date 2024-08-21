package com.lu.weather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lu.weather.common.extension.dataToUICurrentWeather
import com.lu.weather.data.repository.weatherbit.IWeatherRepository
import com.lu.weather.ui.model.UIError
import com.lu.weather.ui.model.UILocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val weatherRepository: IWeatherRepository) :
    ViewModel() {

    init {
        viewModelScope.launch {
            weatherRepository.fetchAllCitiesForecast()
        }
    }

    val locationsFlow = MutableStateFlow(listOf<UILocation>())

    val currentWeathersFlow = weatherRepository.getCitiesFlow().map { listOfCities ->
        listOfCities.filter { it.currentWeather != null }.map {
            it.currentWeather!!.dataToUICurrentWeather(it.cityName, it.timeZone)
        }
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    val errorFlow = weatherRepository.getErrorFlow().map {
        UIError(it.time, it.code)
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000))


    fun onHandleQuery(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                locationsFlow.emit(listOf())
            } else {
                val locations = weatherRepository.searchCity(query)
                val uiLocations = locations.map {
                    UILocation(it.cityName, it.countryCode)
                }
                locationsFlow.emit(uiLocations)
            }
        }
    }
}
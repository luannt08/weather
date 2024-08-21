package com.lu.weather.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lu.weather.common.extension.dataToUICurrentWeather
import com.lu.weather.common.extension.dataToUIDailyForecast
import com.lu.weather.common.extension.dataToUIHourlyForecast
import com.lu.weather.data.repository.weatherbit.IWeatherRepository
import com.lu.weather.ui.model.UIDeleteState
import com.lu.weather.ui.model.UIError
import com.lu.weather.ui.model.UIFullForecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val weatherRepository: IWeatherRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val cityName = savedStateHandle.get<String>("city") ?: ""
    val uiFullForecast by lazy {
        channelFlow {
            weatherRepository.getFocusingCityFlow(cityName)
                .collectLatest { pair ->
                    val cityData = pair.first
                    val isAdded = pair.second

                    val uiFullForecast = UIFullForecast(
                        cityData.currentWeather?.dataToUICurrentWeather(
                            cityData.cityName,
                            cityData.timeZone
                        ),
                        cityData.currentHourlyForecast.map { it.dataToUIHourlyForecast() },
                        cityData.dailyForecasts.map { it.dataToUIDailyForecast() },
                        isAdded
                    )
                    send(uiFullForecast)
                }
        }.flowOn(Dispatchers.IO)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIFullForecast.EMPTY)
    }

    val loadingState  = MutableStateFlow(UIDeleteState.EMPTY)

    val errorFlow = weatherRepository.getErrorFlow().map {
        UIError(it.time, it.code)
    }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000))

    fun onAddNewCity() {
        viewModelScope.launch {
            weatherRepository.addFocusingCityForecastToRealm()
        }
    }

    fun onDeleteCity() {
        viewModelScope.launch {
            loadingState.emit(UIDeleteState(isLoading = true, isDone = false))
            weatherRepository.deleteCityForecast(cityName)
            loadingState.emit(UIDeleteState(isLoading = false, isDone = true))
        }
    }
}
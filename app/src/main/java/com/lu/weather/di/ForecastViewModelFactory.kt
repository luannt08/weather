//package com.lu.weather.di
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.lu.weather.ui.detail.ForecastViewModel
//import dagger.assisted.AssistedFactory
//import javax.inject.Inject
//
//@Suppress("UNCHECKED_CAST")
//class ForecastViewModelFactory @Inject constructor(
//    private val assistedFactory: ForecastViewModel.Factory,
//    private val lat: Double,
//    private val lon: Double
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ForecastViewModel::class.java)) {
//            return assistedFactory.create(lat, lon) as T
//        }
//        throw Exception("Unknown ViewModel!")
//    }
//}
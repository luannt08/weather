package com.lu.weather.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lu.weather.ui.detail.ForecastScreen
import com.lu.weather.ui.detail.ForecastViewModel
import com.lu.weather.ui.home.HomeScreen
import com.lu.weather.ui.home.HomeViewModel
import com.lu.weather.ui.model.UILocation

@Composable
fun WeatherNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destination.HOME_ROUTE) {
        composable(Destination.HOME_ROUTE) {
            val homeViewModel = hiltViewModel<HomeViewModel>()

            HomeScreen(homeViewModel) { uiLocation: UILocation ->
                navController.navigate(Destination.DETAIL_ROUTE + "/${uiLocation.cityName}") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }

                    launchSingleTop = true
                    restoreState = true
                }
            }
        }

        composable(
            Destination.DETAIL_ROUTE + "/{city}",
            arguments = listOf(
                navArgument("city") { NavType.StringType })
        ) {
            val forecastViewModel = hiltViewModel<ForecastViewModel>()
            ForecastScreen(forecastViewModel, onNavigateBack = {
                navController.navigateUp()
            })
        }
    }
}

object Destination {
    const val HOME_ROUTE = "home"
    const val DETAIL_ROUTE = "detail"
}
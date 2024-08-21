package com.lu.weather.ui.detail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lu.weather.R
import com.lu.weather.ui.airQualityString
import com.lu.weather.ui.errorCodeToStr
import com.lu.weather.ui.uvString
import com.lu.weather.ui.model.UICurrentWeather
import com.lu.weather.ui.model.UIDailyForecast
import com.lu.weather.ui.model.UIError
import com.lu.weather.ui.model.UIFullForecast
import com.lu.weather.ui.model.UIHourlyForecast
import com.lu.weather.ui.theme.CardColor
import com.lu.weather.ui.theme.TextRainColor
import com.lu.weather.ui.toHumidStr

@Composable
fun ForecastScreen(
    forecastViewModel: ForecastViewModel = hiltViewModel(), onNavigateBack: () -> Unit
) {
    val uiFullForecast by forecastViewModel.uiFullForecast.collectAsStateWithLifecycle()
    val errorEvent by forecastViewModel.errorFlow.collectAsStateWithLifecycle(
        initialValue = UIError(
            0, 0
        )
    )

    if (uiFullForecast == UIFullForecast.EMPTY) {
        Loading()
        return
    }

    val uiCurrentWeather = uiFullForecast.uiCurrentWeather
    val uiHourlyForecasts = uiFullForecast.uiHourlyForecastOfCurrentDay
    val uiDailyForecasts = uiFullForecast.uiDailyForecast
    val uiDeleteState by forecastViewModel.loadingState.collectAsStateWithLifecycle()


    val colorBg = uiCurrentWeather?.colorBg ?: R.color.storm
    val isLocal = remember {
        mutableStateOf(uiFullForecast.isLocal)
    }

    val errorMessage = stringResource(id = errorEvent.code.errorCodeToStr())
    val dismissStr = stringResource(id = R.string.dismiss)
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = errorEvent) {
        if (errorEvent.code != 0) {
            snackBarHostState.showSnackbar(
                message = errorMessage, actionLabel = dismissStr
            )
        }
    }

    Scaffold(topBar = { Header(isLocal, errorEvent.code != 0, forecastViewModel::onAddNewCity, onNavigateBack) },
        snackbarHost = { SnackbarHost(snackBarHostState) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = colorBg))
                .padding(paddingValues)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                currentWeatherInfo(uiCurrentWeather)
                hourlyForecast(uiHourlyForecasts)
                dailyForecast(uiDailyForecasts)
                weatherInfoGrid(uiCurrentWeather)

                Log.e("Luan", "uiFullFOreCast: $uiFullForecast")
                val showDelete = if (uiFullForecast.isEmpty() || errorEvent.code!= 0) {
                    false
                } else {
                    isLocal.value
                }
                deleteBottom(showDelete, forecastViewModel::onDeleteCity)
            }

            if (uiDeleteState.isLoading) {
                Loading()
            }

            if (uiDeleteState.isDone) {
                onNavigateBack.invoke()
            }
        }
    }
}

@Composable
private fun Header(
    hasAddedState: MutableState<Boolean>, isError: Boolean = false, addNewCityWeather: () -> Unit, navigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            tint = Color.White,
            contentDescription = "back",
            modifier = Modifier
                .size(36.dp)
                .clickable {
                    navigateBack.invoke()
                })
        if (!hasAddedState.value && !isError) {
            Icon(imageVector = Icons.Filled.Add,
                tint = Color.White,
                contentDescription = "add",
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        addNewCityWeather.invoke()
                        hasAddedState.value = true
                    })
        }
    }
}

@Composable
private fun CurrentWeatherInfo(
    uiCurrentWeather: UICurrentWeather
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = uiCurrentWeather.city,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.W500,
                color = Color.White
            )
            Text(
                text = stringResource(id = R.string.degree, uiCurrentWeather.temperature),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.W300,
                color = Color.White
            )

            val inlineContent = mapOf(
                Pair("icon", InlineTextContent(
                    Placeholder(
                        width = 25.sp,
                        height = 25.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                    )
                ) {
                    Icon(
                        painter = painterResource(uiCurrentWeather.icon),
                        "",
                        tint = Color.Unspecified
                    )
                })
            )
            val conditionWithIcon = buildAnnotatedString {
                append("${uiCurrentWeather.weatherCondition}  ")
                appendInlineContent("icon", "[icon]")
            }

            Text(
                text = conditionWithIcon,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                inlineContent = inlineContent
            )
        }
    }
}

private fun LazyListScope.currentWeatherInfo(uiCurrentWeather: UICurrentWeather?) {
    if (uiCurrentWeather != null) {
        item {
            CurrentWeatherInfo(uiCurrentWeather)
        }
    }
}

@Composable
private fun HourlyForecast(hourlyForecasts: List<UIHourlyForecast>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 15.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x4D7C7E82))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.hourly_forecast_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W700,
                color = Color.White,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(hourlyForecasts) { forecast ->
                    HourlyItem(forecast)
                }
            }
        }
    }
}

private fun LazyListScope.hourlyForecast(uiHourlyForecasts: List<UIHourlyForecast>) {
    if (uiHourlyForecasts.isNotEmpty()) {
        item {
            HourlyForecast(uiHourlyForecasts)
        }
    }
}

@Composable
private fun HourlyItem(uiHourlyForecast: UIHourlyForecast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(130.dp)
            .padding(top = 5.dp)
    ) {
        Text(
            text = uiHourlyForecast.timeInString,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp,
            fontWeight = FontWeight.W500,
        )

        if (uiHourlyForecast.chanceOfRain == 0) {
            Icon(
                painter = painterResource(id = uiHourlyForecast.icon),
                contentDescription = "weather_icon",
                tint = Color.Unspecified
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = uiHourlyForecast.icon),
                    contentDescription = "weather_icon",
                    tint = Color.Unspecified,
                )
                Text(
                    text = stringResource(id = R.string.percent, uiHourlyForecast.chanceOfRain),
                    color = TextRainColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }

        Text(
            text = stringResource(id = R.string.degree, uiHourlyForecast.temperature),
            color = Color.White,
            fontWeight = FontWeight.W500,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
            //.padding(top = 5.dp)
        )
    }
}

private fun LazyListScope.dailyForecast(uiDailyForecasts: List<UIDailyForecast>) {
    if (uiDailyForecasts.isEmpty()) {
        return
    }
    item {
        Card(
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
            colors = CardDefaults.cardColors(containerColor = CardColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 15.dp)
        ) {
            Text(
                text = stringResource(id = R.string.day_forecast_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W700,
                color = Color.White,
                modifier = Modifier.padding(top = 15.dp, start = 15.dp, bottom = 10.dp)
            )
        }
    }

    items(uiDailyForecasts) { uiDailyForecast ->
        DailyForecastCard(
            uiDailyForecast = uiDailyForecast, isLast = uiDailyForecast == uiDailyForecasts.last()
        )
    }
}

@Composable
fun DailyForecastCard(uiDailyForecast: UIDailyForecast, isLast: Boolean) {
    Card(
        shape = if (isLast) {
            RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
        } else {
            RoundedCornerShape(0.dp)
        },
        colors = CardDefaults.cardColors(containerColor = CardColor),
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = uiDailyForecast.timeInDay,
                    color = Color.White,
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp
                )

                Box(modifier = Modifier.width(60.dp), contentAlignment = Alignment.Center) {
                    if (uiDailyForecast.chanceOfRain == 0) {
                        Icon(
                            painter = painterResource(id = uiDailyForecast.icon),
                            contentDescription = "weather_icon",
                            tint = Color.Unspecified
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = uiDailyForecast.icon),
                                contentDescription = "weather_icon",
                                tint = Color.Unspecified
                            )

                            //Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = stringResource(
                                    id = R.string.percent, uiDailyForecast.chanceOfRain
                                ),
                                color = TextRainColor,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.W500,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = stringResource(id = R.string.degree, uiDailyForecast.lowTemperature),
                    color = Color.White,
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 15.dp)
                )

                Text(
                    text = stringResource(id = R.string.degree, uiDailyForecast.highTemperature),
                    color = Color.White,
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun WeatherInfoGrid(uiCurrentWeather: UICurrentWeather) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardSize = (screenWidth - 40.dp) / 2

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WeatherInfoCard(
                title = stringResource(id = R.string.uv_index),
                mainValue = uiCurrentWeather.uvIndex.toString(),
                details = stringResource(id = uiCurrentWeather.uvIndex.uvString()),
                size = cardSize,
                icon = R.drawable.uv_index_card
            )
            WeatherInfoCard(
                title = stringResource(id = R.string.sunrise_title),
                mainValue = uiCurrentWeather.sunriseTime,
                details = "Sunset: ${uiCurrentWeather.sunsetTime}",
                size = cardSize,
                icon = R.drawable.sunrise_card
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WeatherInfoCard(
                title = stringResource(id = R.string.wind),
                mainValue = stringResource(id = R.string.mph, uiCurrentWeather.windSpeed),
                size = cardSize,
                icon = R.drawable.wind_speed_card
            )

            WeatherInfoCard(
                title = stringResource(id = R.string.rain_fall),
                mainValue = stringResource(id = R.string.mm, uiCurrentWeather.precipitation),
                details = stringResource(id = R.string.rain_last_24),
                size = cardSize,
                icon = R.drawable.rain_fall_card
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WeatherInfoCard(
                title = stringResource(id = R.string.air_quality),
                mainValue = uiCurrentWeather.airQuality.toString(),
                details = stringResource(id = uiCurrentWeather.airQuality.airQualityString()),
                size = cardSize,
                icon = R.drawable.air_quality_card
            )

            WeatherInfoCard(
                title = stringResource(id = R.string.humidity),
                mainValue = stringResource(id = R.string.percent, uiCurrentWeather.humid),
                details = stringResource(id = uiCurrentWeather.humid.toHumidStr()),
                size = cardSize,
                icon = R.drawable.humid_card
            )
        }
    }
}

private fun LazyListScope.weatherInfoGrid(uiCurrentWeather: UICurrentWeather?) {
    if (uiCurrentWeather != null) {
        item {
            WeatherInfoGrid(uiCurrentWeather)
        }
    }
}

@Composable
fun WeatherInfoCard(
    modifier: Modifier = Modifier,
    title: String,
    mainValue: String,
    details: String = "",
    size: Dp,  // Pass the calculated size to ensure the card is square
    icon: Int
) {
    Card(
        modifier = modifier.size(size),  // Set both width and height to the same size
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x387C7E82))
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Column {
                val inlineContent = mapOf(
                    Pair("icon", InlineTextContent(
                        Placeholder(
                            width = 20.sp,
                            height = 20.sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                        )
                    ) {
                        Icon(
                            painter = painterResource(icon), "", tint = Color.Unspecified
                        )
                    })
                )
                val titleWithIcon = buildAnnotatedString {
                    appendInlineContent("icon", "[icon]")
                    append("$title")
                }
                Text(
                    text = titleWithIcon,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W700,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                    inlineContent = inlineContent
                )
                Text(
                    text = mainValue,
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Text(
                text = details,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W400,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun BottomDelete(onDeleteCity: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(bottom = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(id = R.string.delete),
            color = Color.Red,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.W600,
            modifier = Modifier.clickable {
                onDeleteCity.invoke()
            })
    }
}

private fun LazyListScope.deleteBottom(shouldShow: Boolean, onDeleteCity: () -> Unit) {
    if (shouldShow) {
        item {
            BottomDelete(onDeleteCity)
        }
    }
}


@Composable
private fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0x387C7E82)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@Composable
@Preview
fun PreviewDetail() {
    Loading()
}


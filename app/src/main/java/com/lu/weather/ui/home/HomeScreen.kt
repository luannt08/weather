package com.lu.weather.ui.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lu.weather.R
import com.lu.weather.ui.errorCodeToStr
import com.lu.weather.ui.model.UICurrentWeather
import com.lu.weather.ui.model.UIError
import com.lu.weather.ui.model.UILocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onNavigateToForecastScreen: (UILocation) -> Unit,
) {
    val searchedLocations by homeViewModel.locationsFlow.collectAsStateWithLifecycle()
    val uiCurrentWeathers by homeViewModel.currentWeathersFlow.collectAsStateWithLifecycle()
    val errorEvent by homeViewModel.errorFlow.collectAsStateWithLifecycle(UIError.EMPTY)

    val errorMessage = stringResource(id = errorEvent.code.errorCodeToStr())
    val dismissStr = stringResource(id = R.string.dismiss)
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = errorEvent.time) {
        if (errorEvent.code != 0) {
            snackBarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = dismissStr
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                searchedLocations,
                homeViewModel::onHandleQuery,
                onNavigateToForecastScreen,
            )
            CitiesWeather(uiCurrentWeathers, onNavigateToForecastScreen)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
private fun SearchBar(
    locations: List<UILocation>,
    handleQuery: (String) -> Unit,
    onNavigateToForecastScreen: (UILocation) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val queryFlow = remember { MutableStateFlow("") }
    LaunchedEffect(key1 = Unit) {
        queryFlow.debounce(1000).flowOn(Dispatchers.Default).collectLatest {
            handleQuery.invoke(it)
        }
    }

    SearchBar(query = query,
        onQueryChange = { newQuery ->
            query = newQuery
            queryFlow.value = newQuery
        },
        onSearch = { Log.e("SearchBar", "onSearch - $it") },
        active = active,
        onActiveChange = { active = it },
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (active) Modifier.padding(0.dp) // No padding when active
                else Modifier.padding(
                    horizontal = 16.dp, vertical = 5.dp
                ) // Padding when inactive
            ),
        enabled = true,
        placeholder = {
            Text(
                text = stringResource(id = R.string.search_city_hint),
                color = Color.LightGray,
                style = MaterialTheme.typography.titleMedium
            )
        },
        leadingIcon = {
            if (active) {
                Icon(imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Search Icon",
                    Modifier.clickable {
                        active = false
                        query = ""
                    })
            } else {
                Icon(
                    imageVector = Icons.Default.Search, contentDescription = "Search Icon"
                )
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { query = "" }) {
                    Icon(
                        imageVector = Icons.Default.Clear, contentDescription = "Clear Icon"
                    )
                }
            }
        },
        shape = SearchBarDefaults.inputFieldShape,
        colors = SearchBarDefaults.colors(Color.Transparent),
        windowInsets = SearchBarDefaults.windowInsets,
        interactionSource = remember { MutableInteractionSource() },
        content = {
            if (active) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Adjust height if needed
                ) {
                    items(locations) { location ->
                        Text(text = "${location.cityName}  ${location.countryCode}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable {
                                    onNavigateToForecastScreen.invoke(location)
                                })
                    }
                }
            }
        })
}

@Composable
private fun CitiesWeather(
    weathers: List<UICurrentWeather>,
    onNavigateToForecastScreen: (UILocation) -> Unit
) {
    LazyColumn(Modifier.padding(start = 16.dp, end = 16.dp, top = 5.dp)) {
        items(weathers, key = { item -> item.city }) { uiCurrentWeather ->
            CityWeatherCard(city = uiCurrentWeather.city,
                time = uiCurrentWeather.currentTime,
                temperature = uiCurrentWeather.temperature.toString(),
                condition = uiCurrentWeather.weatherCondition,
                uvIndex = uiCurrentWeather.uvIndex.toString(),
                icon = uiCurrentWeather.icon,
                colorBg = uiCurrentWeather.colorBg,
                modifier = Modifier.clickable {
                    onNavigateToForecastScreen.invoke(
                        UILocation(
                            uiCurrentWeather.city,
                            ""
                        )
                    )
                })
        }
    }
}

@Composable
private fun CityWeatherCard(
    city: String,
    time: String,
    temperature: String,
    condition: String,
    uvIndex: String,
    icon: Int,
    colorBg: Int,
    modifier: Modifier
) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = colorBg))
    ) {
        Column(
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 10.dp, top = 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = city,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.W500,
                        fontSize = 23.sp,
                        color = Color.White
                    )
                    Text(
                        text = time,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.W500,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
                Text(
                    text = stringResource(id = R.string.degree_c, temperature),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 27.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val inlineContent = mapOf(
                    Pair(
                        "icon",
                        InlineTextContent(
                            Placeholder(
                                width = 20.sp,
                                height = 20.sp,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                            )
                        ) {
                            Icon(
                                painter = painterResource(icon),
                                "",
                                tint = Color.Unspecified
                            )
                        }
                    )
                )
                val conditionWithIcon = buildAnnotatedString {
                    append("$condition  ")
                    appendInlineContent("icon", "[icon]")
                }
                Text(
                    text = conditionWithIcon,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W500,
                    fontSize = 18.sp,
                    color = Color.White,
                    inlineContent = inlineContent
                )
                Text(
                    text = stringResource(id = R.string.uv_index_number, uvIndex),
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W500,
                    color = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    CityWeatherCard(
        city = "Ho Chi Minh",
        time = "12:00",
        temperature = "20",
        condition = "Partly Cloudy",
        uvIndex = "2",
        icon = R.drawable.clear_mostly_clear,
        colorBg = R.color.sunny,
        modifier = Modifier
    )
}
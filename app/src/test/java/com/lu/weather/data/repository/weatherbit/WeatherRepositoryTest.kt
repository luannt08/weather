import com.lu.weather.data.model.LocationData
import com.lu.weather.data.model.networking.CurrentWeatherResponse
import com.lu.weather.data.model.networking.DailyForecastResponse
import com.lu.weather.data.model.networking.HourlyForecastResponse
import com.lu.weather.data.model.networking.ItemResponse
import com.lu.weather.data.model.networking.LocationResponse
import com.lu.weather.data.repository.weatherbit.WeatherAPIService
import com.lu.weather.data.repository.weatherbit.WeatherRepository
import com.lu.weather.data.source.IRealmDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.net.UnknownHostException

class WeatherRepositoryTest {

    @Mock
    private lateinit var mockWeatherAPIService: WeatherAPIService

    @Mock
    private lateinit var mockRealmDataSource: IRealmDataSource

    private lateinit var weatherRepository: WeatherRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        weatherRepository = WeatherRepository(mockWeatherAPIService, mockRealmDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test fetchAllCitiesForecast successful`() = runBlocking {

        val mockCities = listOf("Ho Chi Minh")
        `when`(mockRealmDataSource.getAllCitiesLocation()).thenReturn(mockCities)

        `when`(mockWeatherAPIService.fetchCurrentWeather(anyString())).thenReturn(
            Result.success(
                CurrentWeatherResponse(listOf())
            )
        )
        `when`(
            mockWeatherAPIService.fetchHourlyForecast(
                anyString(),
                anyInt()
            )
        ).thenReturn(
            Result.success(
                HourlyForecastResponse(
                    listOf(),
                    30.0,
                    40.0,
                    "Asia/Ho Chi Minh",
                    "Ho Chi Minh"
                )
            )
        )
        `when`(mockWeatherAPIService.fetchDailyForecast(anyString())).thenReturn(
            Result.success(
                DailyForecastResponse(listOf(), 30.0, 40.0, "Asia/Ho Chi Minh", "Ho Chi Minh")
            )
        )

        weatherRepository.fetchAllCitiesForecast()

        verify(mockRealmDataSource, times(mockCities.size)).processCurrentWeather(
            any(),
            any(),
            any(),
            anyString(),
            anyString(),
            anyString(),
            anyString()
        )
    }

    @Test
    fun `test fetchAllCitiesForecast with network error`() = runBlocking {
        val mockCities = listOf("Ho Chi Minh", "Munich")
        `when`(mockRealmDataSource.getAllCitiesLocation()).thenReturn(mockCities)

        `when`(mockWeatherAPIService.fetchCurrentWeather(anyString())).thenReturn(
            Result.failure(
                UnknownHostException()
            )
        )

        weatherRepository.fetchAllCitiesForecast()

        val errorEvent = weatherRepository.getErrorFlow().first()
        assertEquals(1, errorEvent.code)
    }

    @Test
    fun `test searchCity successful`() = runBlocking {
        val query = "New York"
        val items = listOf(ItemResponse("New York", "US", null, 40.7128, -74.0060))
        val locationResponse = LocationResponse(items)

        val locationData = listOf(LocationData("New York", 40.7128, -74.0060, "US"))
        `when`(mockWeatherAPIService.search(query)).thenReturn(Result.success(locationResponse))
        val result = weatherRepository.searchCity(query)
        assertEquals(locationData, result)
    }

    @Test
    fun `test searchCity with error`() = runBlocking {
        val query = "sdfasfsdf"
        `when`(mockWeatherAPIService.search(query)).thenReturn(Result.failure(Exception("City not found")))
        val result = weatherRepository.searchCity(query)

        assertEquals(0, result.size)
    }
}

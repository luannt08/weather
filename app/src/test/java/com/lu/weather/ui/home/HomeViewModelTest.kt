import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lu.weather.data.model.CityData
import com.lu.weather.data.model.CurrentWeatherData
import com.lu.weather.data.model.LocationData
import com.lu.weather.data.model.networking.NetworkingError
import com.lu.weather.data.repository.weatherbit.IWeatherRepository
import com.lu.weather.ui.home.HomeViewModel
import com.lu.weather.ui.model.UIError
import com.lu.weather.ui.model.UILocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var weatherRepository: IWeatherRepository

    private lateinit var homeViewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Mock repository responses
        `when`(weatherRepository.getCitiesFlow()).thenReturn(flowOf(emptyList()))
        `when`(weatherRepository.getErrorFlow()).thenReturn(MutableSharedFlow())

        homeViewModel = HomeViewModel(weatherRepository)
    }

    @Test
    fun `init should fetch all cities forecast`() = runTest {
        verify(weatherRepository).fetchAllCitiesForecast()
    }

    @Test
    fun `locationsFlow should emit empty list when query is empty`() = runTest {
        homeViewModel.onHandleQuery("")

        val result = homeViewModel.locationsFlow.value
        assertEquals(emptyList<UILocation>(), result)
    }

    @Test
    fun `locationsFlow should emit list of UILocation when query is not empty`() = runTest {
        val mockLocations = listOf(
            UILocation("City1", "US"),
            UILocation("City2", "CA")
        )

        `when`(weatherRepository.searchCity("query")).thenReturn(
            listOf(
                LocationData("City1", 0.0, 0.0, "US"),
                LocationData("City2", 0.0, 0.0, "CA")
            )
        )

        homeViewModel.onHandleQuery("query")

        testDispatcher.scheduler.advanceUntilIdle()

        val result = homeViewModel.locationsFlow.value
        assertEquals(mockLocations, result)
    }
}

package com.example.countryexplorer

// Test/CountriesViewModelTest.kt
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.countryexplorer.data.model.Country
import com.example.countryexplorer.data.model.Media
import com.example.countryexplorer.domain.repository.CountriesRepository
import com.example.countryexplorer.domain.usecase.GetCountriesUseCase
import com.example.countryexplorer.presentation.viewmodel.CountriesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
class CountriesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CountriesViewModel
    private val repository = mock(CountriesRepository::class.java)
    private lateinit var getCountriesUseCase: GetCountriesUseCase
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCountriesUseCase = GetCountriesUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `fetchCountries success updates countries LiveData`() = runBlockingTest {
        // Given
        val countries = listOf(
            Country(
                1,
                "BD",
                "Dhaka",
                "BDT",
                "Bangladesh",
                "880",
                162951560,
                Media("flag_url", "emblem_url", "orthographic_url")
            ),
            Country(
                2,
                "BE",
                "Brussels",
                "EUR",
                "Belgium",
                "32",
                11348159,
                Media("flag_url", "emblem_url", "")
            )
        )
        `when`(repository.getCountries()).thenReturn(countries)

        // When
        viewModel = CountriesViewModel(getCountriesUseCase)
        // Advance coroutine until idle
        advanceUntilIdle()

        // Then
        val observed = viewModel.countries.getOrAwaitValue()
        assertEquals(2, observed.size)
    }

    @Test
    fun `searchCountry filters list correctly`() = runBlockingTest {
        val countries = listOf(
            Country(
                1,
                "BD",
                "Dhaka",
                "BDT",
                "Bangladesh",
                "880",
                162951560,
                Media("flag_url", "emblem_url", "orthographic_url")
            ),
            Country(
                2,
                "BE",
                "Brussels",
                "EUR",
                "Belgium",
                "32",
                11348159,
                Media("flag_url", "emblem_url", "")
            )
        )
        `when`(repository.getCountries()).thenReturn(countries)

        viewModel = CountriesViewModel(getCountriesUseCase)
        advanceUntilIdle()

        // When
        viewModel.searchCountry("Belgium")
        val filtered = viewModel.countries.getOrAwaitValue()

        // Then
        assertEquals(1, filtered.size)
        assertEquals("Belgium", filtered[0].name)
    }
}

// Helper extension function to get LiveData values in tests.
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    observeForever(observer)
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}

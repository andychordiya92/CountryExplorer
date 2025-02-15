package com.example.countryexplorer.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countryexplorer.data.model.Country
import com.example.countryexplorer.domain.usecase.GetCountriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val getCountriesUseCase: GetCountriesUseCase
) : ViewModel() {

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>> get() = _countries

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private var allCountries = emptyList<Country>()
    private var currentSearchQuery = ""
    private var currentMaxPopulation: Long? = null

    init {
        fetchCountries()
    }

    private fun fetchCountries() = viewModelScope.launch {
        getCountriesUseCase().onSuccess {
            allCountries = it
            applyFilters()
        }.onFailure {
            _error.value = it.localizedMessage ?: "An error occurred"
        }
    }

    fun searchCountry(query: String) {
        currentSearchQuery = query
        applyFilters()
    }

    fun filterByPopulation(maxPopulation: Long?) {
        currentMaxPopulation = maxPopulation
        applyFilters()
    }

    private fun applyFilters() {
        _countries.value = allCountries.filter {
            it.name.contains(currentSearchQuery, ignoreCase = true) &&
                    (currentMaxPopulation == null || it.population <= currentMaxPopulation!!)
        }
    }
}
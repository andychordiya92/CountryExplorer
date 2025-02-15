package com.example.countryexplorer.data.repository

import com.example.countryexplorer.data.model.Country
import com.example.countryexplorer.data.remote.CountriesApi
import com.example.countryexplorer.data.toCountry
import com.example.countryexplorer.domain.repository.CountriesRepository
import javax.inject.Inject

class CountriesRepositoryImpl @Inject constructor(
    private val api: CountriesApi
) : CountriesRepository {
    override suspend fun getCountries(): List<Country> {
        return api.getCountries().map { it.toCountry() }
    }
}
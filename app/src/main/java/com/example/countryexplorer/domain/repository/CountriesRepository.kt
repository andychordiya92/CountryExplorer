package com.example.countryexplorer.domain.repository

import com.example.countryexplorer.data.model.Country

interface CountriesRepository {
    suspend fun getCountries(): List<Country>
}
package com.example.countryexplorer.data.remote

import com.example.countryexplorer.data.model.Country
import retrofit2.http.GET

interface CountriesApi {
    @GET("countries")
    suspend fun getCountries(): List<Country>
}
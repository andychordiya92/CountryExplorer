package com.example.countryexplorer.domain.usecase

import com.example.countryexplorer.data.model.Country
import com.example.countryexplorer.domain.repository.CountriesRepository
import javax.inject.Inject


class GetCountriesUseCase @Inject constructor(
    private val repository: CountriesRepository
) {
    suspend operator fun invoke(): Result<List<Country>> {
        return try {
            val countries = repository.getCountries()
            Result.success(countries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
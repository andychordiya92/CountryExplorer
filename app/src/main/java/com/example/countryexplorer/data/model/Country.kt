package com.example.countryexplorer.data.model

data class Country(
    val id: Int,
    val abbreviation: String,
    val capital: String,
    val currency: String,
    val name: String,
    val phone: String,
    val population: Long,
    val media: Media
)


package com.example.countryexplorer.data

import com.example.countryexplorer.data.model.Country
import com.example.countryexplorer.data.model.Media

fun Country.toCountry(): Country {
    return Country(
        id = id,
        abbreviation = abbreviation,
        capital = capital,
        currency = currency,
        name = name,
        phone = phone,
        population = population,
        media = Media(
            flag = media.flag,
            emblem = media.emblem,
            orthographic = media.orthographic
        )
    )
}
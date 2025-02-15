package com.example.countryexplorer

fun Long.toMillionFormat(): String {
    return when {
        this < 1_000_000 -> "<1M"
        this < 5_000_000 -> "<5M"
        this < 10_000_000 -> "<10M"
        else -> "${this / 1_000_000}M"
    }
}


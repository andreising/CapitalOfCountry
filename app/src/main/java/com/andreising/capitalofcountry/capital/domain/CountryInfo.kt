package com.andreising.capitalofcountry.capital.domain

data class CountryInfo(
    val name: String,
    val capital: String,
    // in api it is subregion
    val region: String,
    val languages: List<String>,
    val flagLink: String,
    val currency: CountryCurrency
)

package com.andreising.capitalofcountry.capital.domain

interface CapitalRepository {
    suspend fun allCountry(): List<CountryInfo>

    suspend fun countryInfoByCapital(capital: String): List<CountryInfo>
}
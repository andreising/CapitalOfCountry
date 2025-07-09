package com.andreising.capitalofcountry.capital.domain

interface CapitalCloudRepository {
    suspend fun countryInfoByCapital(capital: String): CountryInfo
}

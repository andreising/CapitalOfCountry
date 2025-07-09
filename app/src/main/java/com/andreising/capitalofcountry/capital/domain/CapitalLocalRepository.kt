package com.andreising.capitalofcountry.capital.domain

interface CapitalLocalRepository {
    suspend fun allCountries(): List<CountryInfo>

    suspend fun existCountryWithCapital(capital: String): Boolean

    suspend fun saveCountry(countryInfo: CountryInfo)
}

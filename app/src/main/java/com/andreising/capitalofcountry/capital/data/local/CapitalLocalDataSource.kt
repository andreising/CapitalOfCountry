package com.andreising.capitalofcountry.capital.data.local

import com.andreising.capitalofcountry.capital.data.CountryData

interface CapitalLocalDataSource {
    suspend fun countryByCapital(capital: String): CountryData?

    suspend fun saveCountry(countryData: CountryData)

    suspend fun allCountries(): List<CountryData>
}
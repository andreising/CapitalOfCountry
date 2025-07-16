package com.andreising.capitalofcountry.capital.data.cloud

import com.andreising.capitalofcountry.capital.data.CountryData

interface CapitalCloudDataSource {
    suspend fun countryByCapital(capital: String): CountryData
}
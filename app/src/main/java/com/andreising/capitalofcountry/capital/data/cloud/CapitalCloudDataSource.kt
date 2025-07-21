package com.andreising.capitalofcountry.capital.data.cloud

import com.andreising.capitalofcountry.capital.data.CountryData

interface CapitalCloudDataSource {
    suspend fun countryByCapital(capital: String): CountryData

    class Base(private val service: CapitalService,
        private val cloudErrorHandler: CloudErrorHandler) : CapitalCloudDataSource {
        override suspend fun countryByCapital(capital: String) = try {
            service.countryByCapital(capital).first().toCountryData()
        } catch (e: Exception) {
            throw cloudErrorHandler.handle(e)
        }
    }
}
package com.andreising.capitalofcountry.capital.data

import com.andreising.capitalofcountry.capital.data.local.CapitalLocalDataSource
import com.andreising.capitalofcountry.capital.domain.CountryInfo

interface AllDataBaseCountries {
    suspend fun get(): List<CountryInfo>

    class Base(
        private val local: CapitalLocalDataSource,
    ) : AllDataBaseCountries {
        override suspend fun get() = local.allCountries().map { it.map(DataToDomainMapper()) }
    }
}
package com.andreising.capitalofcountry.capital.data

import com.andreising.capitalofcountry.capital.data.cloud.CapitalCloudDataSource
import com.andreising.capitalofcountry.capital.data.local.CapitalLocalDataSource
import com.andreising.capitalofcountry.capital.domain.CapitalRepository
import com.andreising.capitalofcountry.capital.domain.CountryInfo

class BaseCapitalRepository(
    private val local: CapitalLocalDataSource,
    private val cloud: CapitalCloudDataSource,
    private val allDataBaseCountries: AllDataBaseCountries
) : CapitalRepository {
    override suspend fun allCountry() = allDataBaseCountries.get()

    override suspend fun countryInfoByCapital(capital: String): List<CountryInfo> {
        val lowerCaseCapital = capital.toLowerCase()
        val dataBaseCountry = local.countryByCapital(lowerCaseCapital)
        if (dataBaseCountry!=null) {
            local.saveCountry(dataBaseCountry)
        } else {
            try {
                local.saveCountry(cloud.countryByCapital(lowerCaseCapital))
            } catch (e: Exception) {
                ExceptionDataToDomainMapper.Base.map(e)
            }
        }
        return allDataBaseCountries.get()
    }
}


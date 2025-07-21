package com.andreising.capitalofcountry.capital.domain

import com.andreising.capitalofcountry.R
import com.andreising.capitalofcountry.capital.presentation.ManageResource

interface CapitalInteractor {
    suspend fun countryByCapital(capital: String): CapitalResult
    suspend fun init(): CapitalResult

    class Base(
        private val cloudRepository: CapitalCloudRepository,
        private val localRepository: CapitalLocalRepository,
        private val savedCountries: SavedCountries,
        private val handleError: HandleError
    ) : CapitalInteractor {
        override suspend fun countryByCapital(capital: String): CapitalResult {
            val lowercaseCapital = capital.toLowerCase()
            if (localRepository.existCountryWithCapital(lowercaseCapital)) return savedCountries.countries()
            else return try {
                val info = cloudRepository.countryInfoByCapital(capital)
                localRepository.saveCountry(info)
                return savedCountries.countries()
            } catch (e: Exception) {
                CapitalResult.Failure(handleError.handle(e))
            }
        }

        override suspend fun init() = savedCountries.countries()

    }
}

interface SavedCountries {
    suspend fun countries(): CapitalResult.Success

    class Base(private val localRepository: CapitalLocalRepository) : SavedCountries {
        override suspend fun countries() = CapitalResult.Success(localRepository.allCountries())
    }
}

interface HandleError {
    fun handle(e: Exception): String

    class Base(private val manageResource: ManageResource) : HandleError {
        override fun handle(e: Exception) = when (e) {
            is NoInternetConnectionException -> manageResource.string(R.string.no_connection_message)
            is IllegalCapitalException -> manageResource.string(R.string.illegal_capital)
            else -> manageResource.string(R.string.service_unavailable)
        }
    }

}
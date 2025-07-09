package com.andreising.capitalofcountry.capital.domain

interface CapitalInteractor {
    suspend fun countryByCapital(capital: String): CapitalResult
    suspend fun init(): CapitalResult

    class Base(
        private val handler: CapitalExceptionHandler,
        private val capitalRepository: CapitalRepository
    ) : CapitalInteractor {
        override suspend fun countryByCapital(capital: String): CapitalResult {
            return try {
                CapitalResult.Success(capitalRepository.countryInfoByCapital(capital))
            } catch (e: Exception) {
                CapitalResult.Failure(handler.handle(e))
            }
        }

        override suspend fun init() = CapitalResult.Success(capitalRepository.allCountry())

    }
}


package com.andreising.capitalofcountry.capital.domain

interface CapitalInteractor {
    suspend fun countryByCapital(capital: String) : CapitalResult
    suspend fun init() : CapitalResult

    class Base(private val capitalRepository: CapitalRepository) : CapitalInteractor {

    }
}
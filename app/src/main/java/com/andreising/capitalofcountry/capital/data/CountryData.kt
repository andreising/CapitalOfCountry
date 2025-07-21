package com.andreising.capitalofcountry.capital.data

import kotlin.String

data class CountryData(
    private val name: String,
    private val capital: String,
    private val region: String,
    private val languages: List<String>,
    private val flagLink: String,
    private val currency: String,
    private val currencySymbol: String
) {

    interface Mapper<T> {
        fun map(
            name: String,
            capital: String,
            region: String,
            languages: List<String>,
            flagLink: String,
            currency: String,
            currencySymbol: String
        ): T

        object CreateCountryData : Mapper<CountryData> {
            override fun map(
                name: String,
                capital: String,
                region: String,
                languages: List<String>,
                flagLink: String,
                currency: String,
                currencySymbol: String
            ) = CountryData(name, capital, region, languages, flagLink, currency, currencySymbol)

        }
    }

    fun <T> map(mapper: Mapper<T>) = mapper.map(
        name = name,
        capital = capital,
        region = region,
        languages = languages,
        flagLink = flagLink,
        currency = currency,
        currencySymbol = currencySymbol
    )
}

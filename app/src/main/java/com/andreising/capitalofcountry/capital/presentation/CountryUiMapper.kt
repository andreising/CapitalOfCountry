package com.andreising.capitalofcountry.capital.presentation

import com.andreising.capitalofcountry.capital.domain.CountryInfo

class CountryUiMapper : Mapper<CountryUi, CountryInfo> {
    override fun map(source: CountryInfo) = CountryUi(
        name = source.name,
        capital = source.capital,
        region = source.region,
        languages = listToString(source.languages),
        flagLink = source.flagLink,
        currencyInfo = source.currency.toString()
    )

    private fun listToString(list: List<String>): String {
        return list.joinToString(separator = ", ")
    }
}
package com.andreising.capitalofcountry.capital.data

import com.andreising.capitalofcountry.capital.domain.CountryCurrency
import com.andreising.capitalofcountry.capital.domain.CountryInfo

class DataToDomainMapper : CountryData.Mapper<CountryInfo> {
    override fun map(
        name: String,
        capital: String,
        region: String,
        languages: List<String>,
        flagLink: String,
        currency: String,
        currencySymbol: String
    ) = CountryInfo(
        name = name,
        capital = capital,
        region = region,
        languages = languages,
        flagLink = flagLink,
        currency = CountryCurrency(currency, currencySymbol)
    )
}
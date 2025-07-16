package com.andreising.capitalofcountry.capital.data.local

import com.andreising.capitalofcountry.capital.data.CountryData

class CapitalInDataBaseMapper(
    private val capitalFromDataBase: String
) : CountryData.Mapper<Boolean> {
    override fun map(
        name: String,
        capital: String,
        region: String,
        languages: List<String>,
        flagLink: String,
        currency: String,
        currencySymbol: String
    ) = capital.toLowerCase() == capitalFromDataBase.toLowerCase()
}
package com.andreising.capitalofcountry.capital.data.cloud

import com.andreising.capitalofcountry.capital.data.CountryData
import com.google.gson.annotations.SerializedName

data class CountryResponse(
    @SerializedName("name")
    private val name: Name,
    @SerializedName("capital")
    private val capital: List<String>,
    @SerializedName("region")
    private val region: String,
    @SerializedName("languages")
    private val languages: Map<String, String>,
    @SerializedName("flags")
    private val flags: Flags,
    @SerializedName("currencies")
    private val currencies: Map<String, Currency>
) {
    fun toCountryData() = CountryData.Mapper.CreateCountryData.map(
        name = name.common,
        capital = capital.first(),
        region = region,
        languages = languages.values.toList(),
        flagLink = flags.png,
        //todo fix this
        currency = currencies.values.toList().first().name,
        currencySymbol = currencies.values.toList().first().symbol
    )
}

data class Name(
    @SerializedName("common")
    val common: String
)

data class Flags(
    @SerializedName("png")
    val png: String
)

data class Currency(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("name")
    val name: String
)
package com.andreising.capitalofcountry.capital.domain

data class CountryCurrency(
    val name: String,
    val symbol: String
) {
    override fun toString(): String {
        return "$name $symbol"
    }
}

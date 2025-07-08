package com.andreising.capitalofcountry.capital.domain

sealed class CapitalResult {

    interface Mapper<T> {
        fun map(list: List<CountryInfo>, message: String): T
    }
    abstract fun <T> map(mapper: Mapper<T>): T

    //todo think about returned type when just check internet connection
    data class Success(private val data: List<CountryInfo> = emptyList()) : CapitalResult() {
        override fun <T> map(mapper: Mapper<T>) = mapper.map(data, "")
    }

    data class Failure(private val message: String) : CapitalResult() {
        override fun <T> map(mapper: Mapper<T>) = mapper.map(emptyList(), message)
    }
}
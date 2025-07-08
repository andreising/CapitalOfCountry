package com.andreising.capitalofcountry.capital.presentation

interface Mapper<R, S> {

    fun map(source: S) : R

    interface Unit<S> : Mapper<kotlin.Unit, S> {
        override fun map(source: S) = Unit
    }
}
package com.andreising.capitalofcountry.capital.presentation

sealed class UiState {

    interface Mapper<T> {
        fun map(message: String): T
    }

    abstract fun <T> map(mapper: Mapper<T>): T

    data object Success : UiState() {
        override fun <T> map(mapper: Mapper<T>) = mapper.map("")
    }

    data class Error(private val message: String) : UiState() {
        override fun <T> map(mapper: Mapper<T>) = mapper.map(message)
    }
}
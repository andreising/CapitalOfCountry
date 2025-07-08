package com.andreising.capitalofcountry.capital.presentation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface CoroutineDispatchers {
    fun io(): CoroutineDispatcher
    fun ui(): CoroutineDispatcher

    class Base : CoroutineDispatchers {
        override fun io(): CoroutineDispatcher {
            return Dispatchers.IO
        }

        override fun ui(): CoroutineDispatcher {
            return Dispatchers.Main.immediate
        }

    }
}
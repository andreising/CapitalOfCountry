package com.andreising.capitalofcountry.capital.data.cloud

import retrofit2.HttpException

interface CloudErrorHandler {
    fun handle(e: Exception): Exception

    class Base() : CloudErrorHandler {
        override fun handle(e: Exception): Exception {
            return when(e) {
                is HttpException -> IllegalRequestValue()
                else -> e
            }
        }

    }
}
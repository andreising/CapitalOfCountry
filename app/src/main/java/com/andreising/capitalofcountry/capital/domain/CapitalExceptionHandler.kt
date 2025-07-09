package com.andreising.capitalofcountry.capital.domain

import com.andreising.capitalofcountry.R
import com.andreising.capitalofcountry.capital.presentation.ManageResource

interface CapitalExceptionHandler {
    fun handle(e: Exception): String

    class Base(
        private val manageResource: ManageResource
    ) : CapitalExceptionHandler {
        override fun handle(e: Exception) = when (e) {
            is NoInternetConnectionException -> manageResource.string(R.string.no_internet)
            is IllegalCapitalName -> manageResource.string(R.string.wrong_capital)
            else -> manageResource.string(R.string.unavailable_service)
        }
    }
}
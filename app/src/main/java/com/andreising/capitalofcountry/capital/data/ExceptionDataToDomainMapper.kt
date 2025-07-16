package com.andreising.capitalofcountry.capital.data

import com.andreising.capitalofcountry.capital.data.cloud.IllegalRequestValue
import com.andreising.capitalofcountry.capital.domain.IllegalCapitalName
import com.andreising.capitalofcountry.capital.domain.NoInternetConnectionException
import com.andreising.capitalofcountry.capital.domain.ServiceUnavailableException
import java.net.UnknownHostException

interface ExceptionDataToDomainMapper {
    fun map(e: Exception)

    object Base : ExceptionDataToDomainMapper {
        override fun map(e: Exception) {
            throw when (e) {
                is UnknownHostException -> NoInternetConnectionException()
                is IllegalRequestValue -> IllegalCapitalName()
                else -> ServiceUnavailableException()
            }
        }
    }
}
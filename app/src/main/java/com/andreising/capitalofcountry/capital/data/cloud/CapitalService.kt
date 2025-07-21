package com.andreising.capitalofcountry.capital.data.cloud

import retrofit2.http.GET
import retrofit2.http.Path

interface CapitalService {

    @GET("capital/{capital}")
    suspend fun countryByCapital(@Path("capital") capital: String) : List<CountryResponse>
}
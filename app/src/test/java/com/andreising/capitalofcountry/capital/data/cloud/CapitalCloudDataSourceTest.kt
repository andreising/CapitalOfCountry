package com.andreising.capitalofcountry.capital.data.cloud

import com.andreising.capitalofcountry.capital.data.CountryData
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.UnknownHostException
import kotlin.jvm.java

class CapitalCloudDataSourceTest {

    private lateinit var dataSource: CapitalCloudDataSource.Base

    @Before
    @Test
    fun `set up`() {
        val service = Retrofit.Builder()
            .baseUrl("https://restcountries.com/v3.1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CapitalService::class.java)
        val handler = CloudErrorHandler.Base()
        dataSource = CapitalCloudDataSource.Base(service = service, handler)
    }

    @Test
    fun `get country by capital`() = runBlocking {
        val actual: CountryData = dataSource.countryByCapital("berlin")
        val expected = CountryData(
            name = "Germany",
            capital = "Berlin",
            region = "Europe",
            languages = listOf("German"),
            flagLink = "https://flagcdn.com/w320/de.png",
            currency = "Euro",
            currencySymbol = "€"
        )

        assertEquals(expected, actual)
    }

    @Test(expected = IllegalRequestValue::class)
    fun `get country by wrong capital`() = runBlocking {
        val actual = dataSource.countryByCapital("berlni")
    }

    //run this test without internet
    @Test(expected = UnknownHostException::class)
    fun `check unknown host exception`() = runBlocking {
        val actual = dataSource.countryByCapital("berlin")
    }
}
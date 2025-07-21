package com.andreising.capitalofcountry.capital.data

import com.andreising.capitalofcountry.capital.data.cloud.CapitalCloudDataSource
import com.andreising.capitalofcountry.capital.data.cloud.IllegalRequestValue
import com.andreising.capitalofcountry.capital.data.local.CapitalInDataBaseMapper
import com.andreising.capitalofcountry.capital.data.local.CapitalLocalDataSource
import com.andreising.capitalofcountry.capital.domain.CapitalRepository
import com.andreising.capitalofcountry.capital.domain.CapitalResult
import com.andreising.capitalofcountry.capital.domain.CountryCurrency
import com.andreising.capitalofcountry.capital.domain.CountryInfo
import com.andreising.capitalofcountry.capital.domain.IllegalCapitalException
import com.andreising.capitalofcountry.capital.domain.NoInternetConnectionException
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

class BaseCapitalRepositoryTest {
    private lateinit var repository: CapitalRepository
    private lateinit var cloudDataSource: TestCloudDataSource
    private lateinit var localDataSource: TestLocalDataSource

    @Before
    @Test
    fun `set up`() {
        cloudDataSource = TestCloudDataSource()
        localDataSource = TestLocalDataSource()
        repository = BaseCapitalRepository(
            local = localDataSource,
            cloud = cloudDataSource,
            allDataBaseCountries = AllDataBaseCountries.Base(localDataSource)
        )
    }

    @Test
    fun `get empty list`() = runBlocking {
        //prepare
        localDataSource.setList(emptyList())

        // action
        val expected = listOf<CountryData>()
        val actual = repository.allCountry()

        // check
        assertEquals(1, localDataSource.allCountriesCalled)
        assertEquals(0, cloudDataSource.calledCountries.size)
        assertEquals(expected, actual)
    }

    @Test
    fun `init and check success with list with some items`() = runBlocking {
        //prepare
        localDataSource.setList(
            listOf(
                CountryData(
                    "",
                    "",
                    "",
                    listOf("", ""),
                    "",
                    "",
                    ""
                ),
                CountryData(
                    "Monaco",
                    "Monaco",
                    "Europe",
                    listOf("French"),
                    "flag link",
                    "Euro",
                    "symbol"
                )
            )
        )

        // action
        val expected = listOf(
            CountryInfo(
                "",
                "",
                "",
                listOf("", ""),
                "",
                CountryCurrency("", "")
            ),
            CountryInfo(
                "Monaco",
                "Monaco",
                "Europe",
                listOf("French"),
                "flag link",
                CountryCurrency(
                    "Euro",
                    "symbol"
                )

            )
        )
        val actual = repository.allCountry()

        // check
        assertEquals(1, localDataSource.allCountriesCalled)
        assertEquals(0, cloudDataSource.calledCountries.size)
        assertEquals(expected, actual)
    }

    @Test
    fun `fetch info by uppercase capital expected success with info, no element in data before`() =
        runBlocking {
            // prepare
            localDataSource.setList(emptyList())
            cloudDataSource.setExpectedCountry(
                CountryData(
                    "Germany",
                    "Berlin",
                    "Europe",
                    listOf("German"),
                    "flag link",
                    "Euro",
                    "symbol"
                )
            )

            // action
            val expected = listOf(
                CountryInfo(
                    "Germany",
                    "Berlin",
                    "Europe",
                    listOf("German"),
                    "flag link",
                    CountryCurrency(
                        "Euro",
                        "symbol"
                    )
                )
            )
            val actual = repository.countryInfoByCapital("BERLIN")

            // check
            assertEquals(1, localDataSource.countryByCapitalCalled.size)
            assertEquals(null, localDataSource.countryByCapitalCalled[0])

            assertEquals(1, cloudDataSource.calledCountries.size)
            assertEquals(
                CountryData(
                    "Germany",
                    "Berlin",
                    "Europe",
                    listOf("German"),
                    "flag link",
                    "Euro",
                    "symbol"
                ), cloudDataSource.calledCountries[0]
            )

            assertEquals(1, localDataSource.saveCountryCalled)

            assertEquals(1, localDataSource.allCountriesCalled)

            assertEquals(expected, actual)
        }

    @Test
    fun `fetch info by lowercase capital expected success with info`() = runBlocking {
        // prepare
        localDataSource.setList(emptyList())
        cloudDataSource.setExpectedCountry(
            CountryData(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                "Euro",
                "symbol"
            )
        )

        // action
        val expected = listOf(
            CountryInfo(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                CountryCurrency(
                    "Euro",
                    "symbol"
                )
            )
        )

        val actual = repository.countryInfoByCapital("berlin")

        // check
        assertEquals(1, localDataSource.countryByCapitalCalled.size)
        assertEquals(null, localDataSource.countryByCapitalCalled[0])

        assertEquals(1, cloudDataSource.calledCountries.size)
        assertEquals(
            CountryData(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                "Euro",
                "symbol"
            ), cloudDataSource.calledCountries[0]
        )

        assertEquals(1, localDataSource.saveCountryCalled)

        assertEquals(1, localDataSource.allCountriesCalled)

        assertEquals(expected, actual)
    }

    @Test(expected = NoInternetConnectionException::class)
    fun `fetch info by capital without internet connection, expect exception`() = runBlocking {
        // prepare
        localDataSource.setList(emptyList())
        cloudDataSource.setConditions(connection = false)

        // action
        repository.countryInfoByCapital("berlin")

        // check
        assertEquals(1, localDataSource.countryByCapitalCalled.size)
        assertEquals(null, localDataSource.countryByCapitalCalled[0])

        assertEquals(1, cloudDataSource.countryByCapitalCalledCount)
        assertEquals(0, cloudDataSource.calledCountries.size)
    }

    @Test(expected = IllegalCapitalException::class)
    fun `fetch info by wrong capital, expect exception`() = runBlocking {
        // prepare
        localDataSource.setList(emptyList())
        cloudDataSource.setConditions(isRightCapital = false)

        // action
        val expected = CapitalResult.Failure(message = "Unavailable capital")
        val actual = repository.countryInfoByCapital("berlni")

        // check
        assertEquals(1, localDataSource.countryByCapitalCalled.size)
        assertEquals(null, localDataSource.countryByCapitalCalled[0])

        assertEquals(1, cloudDataSource.countryByCapitalCalledCount)
        assertEquals(0, cloudDataSource.calledCountries.size)
        assertEquals(expected, actual)
    }

    @Test
    fun `fetch info by capital which country in data base`() = runBlocking {
        // prepare
        localDataSource.setList(
            listOf(
                CountryData(
                    "France",
                    "Paris",
                    "Europe",
                    listOf("French"),
                    "france flag link",
                    "Euro",
                    "symbol"
                ),
                CountryData(
                    "Germany",
                    "Berlin",
                    "Europe",
                    listOf("German"),
                    "flag link",
                    "Euro",
                    "symbol"
                )
            )
        )

        //action
        val expected = listOf(
            CountryInfo(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                CountryCurrency("Euro", "symbol")
            ),
            CountryInfo(
                "France",
                "Paris",
                "Europe",
                listOf("French"),
                "france flag link",
                CountryCurrency("Euro", "symbol")
            )

        )
        val actual = repository.countryInfoByCapital("berlin")

        //check
        assertEquals(1, localDataSource.countryByCapitalCalled.size)
        assertEquals(
            CountryData(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                "Euro",
                "symbol"
            ), localDataSource.countryByCapitalCalled[0]
        )

        assertEquals(0, cloudDataSource.countryByCapitalCalledCount)

        assertEquals(1, localDataSource.allCountriesCalled)

        assertEquals(expected, actual)
    }

    @Test
    fun `fetch info by capital, db is not empty, expected success`() = runBlocking {
        // prepare
        localDataSource.setList(
            listOf(
                CountryData(
                    "France",
                    "Paris",
                    "Europe",
                    listOf("French"),
                    "french flag link",
                    "Euro", "symbol"
                )
            )
        )
        cloudDataSource.setExpectedCountry(
            CountryData(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                "Euro", "symbol"
            )
        )

        //action
        val expected = listOf(
            CountryInfo(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                CountryCurrency("Euro", "symbol")
            ), CountryInfo(
                "France",
                "Paris",
                "Europe",
                listOf("French"),
                "french flag link",
                CountryCurrency("Euro", "symbol")
            )

        )
        val actual = repository.countryInfoByCapital("berlin")

        // check
        assertEquals(1, localDataSource.countryByCapitalCalled.size)
        assertEquals(null, localDataSource.countryByCapitalCalled[0])

        assertEquals(1, cloudDataSource.calledCountries.size)
        assertEquals(
            CountryData(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                "Euro", "symbol"
            ), cloudDataSource.calledCountries[0]
        )
        assertEquals(1, localDataSource.saveCountryCalled)

        assertEquals(1, localDataSource.allCountriesCalled)

        assertEquals(expected, actual)
    }

    private class TestLocalDataSource : CapitalLocalDataSource {
        val countryByCapitalCalled = mutableListOf<CountryData?>()
        var saveCountryCalled = 0
        var allCountriesCalled = 0
        private val data = mutableListOf<CountryData>()

        fun setList(list: List<CountryData>) {
            data.clear()
            data.addAll(list)
        }

        override suspend fun countryByCapital(capital: String): CountryData? {
            val result =
                data.find { it.map(CapitalInDataBaseMapper(capital)) }
            countryByCapitalCalled.add(result)
            return result
        }

        override suspend fun saveCountry(countryData: CountryData) {
            saveCountryCalled++
            data.remove(countryData)
            data.addFirst(countryData)
        }

        override suspend fun allCountries(): List<CountryData> {
            allCountriesCalled++
            return data
        }
    }

    private inner class TestCloudDataSource : CapitalCloudDataSource {

        private var expected = defaultCountryData

        private var gotConnection = true

        private var rightCapital = true

        var countryByCapitalCalledCount = 0

        val calledCountries = mutableListOf<CountryData>()

        fun setConditions(connection: Boolean = true, isRightCapital: Boolean = true) {
            gotConnection = connection
            rightCapital = isRightCapital
        }

        fun setExpectedCountry(countryData: CountryData) {
            expected = countryData
        }

        override suspend fun countryByCapital(capital: String): CountryData {
            countryByCapitalCalledCount++
            if (!gotConnection) throw UnknownHostException()
            if (!rightCapital) throw IllegalRequestValue()
            calledCountries.add(expected)
            return expected
        }
    }

    private val defaultCountryData = CountryData(
        "Monaco",
        "Monaco",
        "Europe",
        listOf("French"),
        "flag link",
        "Euro",
        "symbol"
    )
}
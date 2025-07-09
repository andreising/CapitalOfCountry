package com.andreising.capitalofcountry.capital.domain

import com.andreising.capitalofcountry.R
import com.andreising.capitalofcountry.capital.presentation.ManageResource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CapitalInteractorTest {

    private lateinit var interactor: CapitalInteractor
    private lateinit var repository: TestCapitalRepository

    @Before
    @Test
    fun `set up`() {
        repository = TestCapitalRepository()
        interactor = CapitalInteractor.Base(
            capitalRepository = repository,
            handler = CapitalExceptionHandler.Base(TestManageResource())
        )
    }

    @Test
    fun `init and check success with empty list`() = runBlocking {
        //prepare
        repository.setNewCountryList(emptyList())

        // action
        val expected = CapitalResult.Success()
        val actual = interactor.init()

        // check
        assertEquals(1, repository.allCountryCalled)
        assertEquals(expected, actual)
    }

    @Test
    fun `init and check success with list with some items`() = runBlocking {
        //prepare
        repository.setNewCountryList(
            listOf(
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
                    CountryCurrency("Euro", "symbol")
                )
            )
        )

        // action
        val expected = CapitalResult.Success(
            data = listOf(
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
                    CountryCurrency("Euro", "symbol")
                )
            )
        )
        val actual = interactor.init()

        // check
        assertEquals(1, repository.allCountryCalled)
        assertEquals(expected, actual)
    }

    @Test
    fun `fetch info by right capital expected success with info`() = runBlocking {
        // prepare
        repository.setNewCountryInfo(
            CountryInfo(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                CountryCurrency("Euro", "symbol")
            )
        )

        // action
        val expected = CapitalResult.Success(
            data = listOf(
                CountryInfo(
                    "Germany",
                    "Berlin",
                    "Europe",
                    listOf("German"),
                    "flag link",
                    CountryCurrency("Euro", "symbol")
                )
            )
        )
        val actual = interactor.countryByCapital("berlin")

        // check
        assertEquals(1, repository.countryInfoListCalled.size)
        assertEquals(
            CountryInfo(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                CountryCurrency("Euro", "symbol")
            ), repository.countryInfoListCalled[0]
        )
        assertEquals(1, repository.byCapitalCalled)
        assertEquals(expected, actual)
    }

    @Test
    fun `fetch info by capital without internet connection, expect exception`() = runBlocking {
        // prepare
        repository.expectingError(error = NoInternetConnectionException())

        // action
        val expected = CapitalResult.Failure(message = "No internet connection")
        val actual = interactor.countryByCapital("berlin")

        // check
        assertEquals(0, repository.countryInfoListCalled.size)
        assertEquals(1, repository.byCapitalCalled)
        assertEquals(expected, actual)
    }

    @Test
    fun `fetch info by wrong capital, expect exception`() = runBlocking {
        // prepare
        repository.expectingError(error = IllegalCapitalName())

        // action
        val expected = CapitalResult.Failure(message = "Unavailable capital")
        val actual = interactor.countryByCapital("berlni")

        // check
        assertEquals(0, repository.countryInfoListCalled.size)
        assertEquals(1, repository.byCapitalCalled)
        assertEquals(expected, actual)
    }

    private class TestCapitalRepository : CapitalRepository {

        private val allCountries = mutableListOf<CountryInfo>()

        val countryInfoListCalled = mutableListOf<CountryInfo>()

        private var expectedCountryInfo = CountryInfo(
            "",
            "",
            "",
            listOf("", ""),
            "",
            CountryCurrency("", "")
        )

        private var expectedError: Exception? = null

        var allCountryCalled = 0

        var byCapitalCalled = 0

        fun expectingError(error: Exception) {
            expectedError = error
        }

        fun setNewCountryInfo(countryInfo: CountryInfo) {
            expectedCountryInfo = countryInfo
        }

        fun setNewCountryList(list: List<CountryInfo>) {
            allCountries.clear()
            allCountries.addAll(list)
        }

        override suspend fun allCountry(): List<CountryInfo> {
            allCountryCalled++
            return allCountries
        }

        override suspend fun countryInfoByCapital(capital: String): List<CountryInfo> {
            byCapitalCalled++
            expectedError?.let { throw it }
            countryInfoListCalled.add(expectedCountryInfo)
            allCountries.addFirst(expectedCountryInfo)
            return allCountries
        }
    }

    private class TestManageResource : ManageResource {
        override fun string(id: Int): String {
            return when (id) {
                R.string.no_internet -> "No internet connection"
                R.string.wrong_capital -> "Unavailable capital"
                else -> ""
            }
        }
    }
}
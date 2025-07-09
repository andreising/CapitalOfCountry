package com.andreising.capitalofcountry.capital.domain

import com.andreising.capitalofcountry.R
import com.andreising.capitalofcountry.capital.presentation.ManageResource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CapitalInteractorTest {

    private lateinit var interactor: CapitalInteractor
    private lateinit var cloudRepository: TestCapitalCloudRepository
    private lateinit var localRepository: TestCapitalLocalRepository

    @Before
    @Test
    fun `set up`() {
        cloudRepository = TestCapitalCloudRepository()
        localRepository = TestCapitalLocalRepository()
        val manageResource = TestManageResources()
        interactor = CapitalInteractor.Base(
            cloudRepository = cloudRepository,
            localRepository = localRepository,
            savedCountries = SavedCountries.Base(localRepository),
            handleError = HandleError.Base(manageResource)
        )
    }

    @Test
    fun `init and check success with empty list`() = runBlocking {
        //prepare
        localRepository.setNewCountryList(emptyList())

        // action
        val expected = CapitalResult.Success()
        val actual = interactor.init()

        // check
        assertEquals(1, localRepository.allCountryCalled)
        assertEquals(expected, actual)
    }

    @Test
    fun `init and check success with list with some items`() = runBlocking {
        //prepare
        localRepository.setNewCountryList(
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
        assertEquals(1, localRepository.allCountryCalled)
        assertEquals(expected, actual)
    }

    @Test
    fun `fetch info by uppercase capital expected success with info, no element in data before`() =
        runBlocking {
            // prepare
            localRepository.setNewCountryList(emptyList())
            cloudRepository.setNewCountryInfo(
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
            val actual = interactor.countryByCapital("BERLIN")

            // check
            assertEquals(1, localRepository.existedCountryCalled.size)
            assertEquals(false, localRepository.existedCountryCalled[0])

            assertEquals(1, cloudRepository.byCapitalCalled)
            assertEquals(1, cloudRepository.countryInfoListCalled.size)
            assertEquals(
                CountryInfo(
                    "Germany",
                    "Berlin",
                    "Europe",
                    listOf("German"),
                    "flag link",
                    CountryCurrency("Euro", "symbol")
                ), cloudRepository.countryInfoListCalled[0]
            )

            assertEquals(1, localRepository.saveCountryCalled)

            assertEquals(1, localRepository.allCountries.size)
            assertEquals(
                CountryInfo(
                    "Germany",
                    "Berlin",
                    "Europe",
                    listOf("German"),
                    "flag link",
                    CountryCurrency("Euro", "symbol")
                ), localRepository.allCountries[0]
            )

            assertEquals(expected, actual)
        }

    @Test
    fun `fetch info by lowercase capital expected success with info`() = runBlocking {
        // prepare
        localRepository.setNewCountryList(emptyList())
        cloudRepository.setNewCountryInfo(
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
        assertEquals(1, localRepository.existedCountryCalled.size)
        assertEquals(false, localRepository.existedCountryCalled[0])

        assertEquals(1, cloudRepository.byCapitalCalled)
        assertEquals(1, cloudRepository.countryInfoListCalled.size)
        assertEquals(
            CountryInfo(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                CountryCurrency("Euro", "symbol")
            ), cloudRepository.countryInfoListCalled[0]
        )

        assertEquals(1, localRepository.saveCountryCalled)

        assertEquals(1, localRepository.allCountries.size)
        assertEquals(
            CountryInfo(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                CountryCurrency("Euro", "symbol")
            ), localRepository.allCountries[0]
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `fetch info by capital without internet connection, expect exception`() = runBlocking {
        // prepare
        localRepository.setNewCountryList(emptyList())
        cloudRepository.expectingError(error = NoInternetConnection())

        // action
        val expected = CapitalResult.Failure(message = "No internet connection")
        val actual = interactor.countryByCapital("berlin")

        // check
        assertEquals(1, localRepository.existedCountryCalled.size)
        assertEquals(false, localRepository.existedCountryCalled[0])

        assertEquals(1, cloudRepository.byCapitalCalled)
        assertEquals(0, cloudRepository.countryInfoListCalled.size)

        assertEquals(expected, actual)
    }

    @Test
    fun `fetch info by wrong capital, expect exception`() = runBlocking {
        // prepare
        localRepository.setNewCountryList(emptyList())
        cloudRepository.expectingError(error = IllegalCapitalName())

        // action
        val expected = CapitalResult.Failure(message = "Unavailable capital")
        val actual = interactor.countryByCapital("berlni")

        // check
        assertEquals(1, localRepository.existedCountryCalled.size)
        assertEquals(false, localRepository.existedCountryCalled[0])

        assertEquals(1, cloudRepository.byCapitalCalled)
        assertEquals(0, cloudRepository.countryInfoListCalled.size)
        assertEquals(expected, actual)
    }

    @Test
    fun `fetch info by capital which country in data base`() = runBlocking {
        // prepare
        localRepository.setNewCountryList(
            listOf(
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

        //action
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

        //check
        assertEquals(1, localRepository.existedCountryCalled.size)
        assertEquals(true, localRepository.existedCountryCalled[0])

        assertEquals(0, cloudRepository.byCapitalCalled)

        assertEquals(1, localRepository.allCountryCalled)

        assertEquals(expected, actual)
    }

    @Test
    fun `fetch info by capital, db is not empty, expected success`() = runBlocking {
        // prepare
        localRepository.setNewCountryList(
            listOf(
                CountryInfo(
                    "France",
                    "Paris",
                    "Europe",
                    listOf("French"),
                    "french flag link",
                    CountryCurrency("Euro", "symbol")
                )
            )
        )
        cloudRepository.setNewCountryInfo(
            CountryInfo(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                CountryCurrency("Euro", "symbol")
            )
        )

        //action
        val expected = CapitalResult.Success(
            data = listOf(
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
        )
        val actual = interactor.countryByCapital("berlin")

        // check
        assertEquals(1, localRepository.existedCountryCalled.size)
        assertEquals(false, localRepository.existedCountryCalled[0])

        assertEquals(1, cloudRepository.byCapitalCalled)
        assertEquals(1, cloudRepository.countryInfoListCalled.size)
        assertEquals(
            CountryInfo(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                CountryCurrency("Euro", "symbol")
            ), cloudRepository.countryInfoListCalled[0]
        )

        assertEquals(1, localRepository.saveCountryCalled)

        assertEquals(2, localRepository.allCountries.size)
        assertEquals(
            CountryInfo(
                "Germany",
                "Berlin",
                "Europe",
                listOf("German"),
                "flag link",
                CountryCurrency("Euro", "symbol")
            ), localRepository.allCountries[0]
        )

        assertEquals(expected, actual)
    }

    private class TestCapitalCloudRepository : CapitalCloudRepository {
        private var expectedCountryInfo = CountryInfo(
            "",
            "",
            "",
            listOf("", ""),
            "",
            CountryCurrency("", "")
        )

        val countryInfoListCalled = mutableListOf<CountryInfo>()

        private var expectedError: Exception? = null

        var byCapitalCalled = 0

        fun expectingError(error: Exception) {
            expectedError = error
        }

        fun setNewCountryInfo(countryInfo: CountryInfo) {
            expectedCountryInfo = countryInfo
        }

        override suspend fun countryInfoByCapital(capital: String): CountryInfo {
            byCapitalCalled++
            expectedError?.let { throw it }
            countryInfoListCalled.add(expectedCountryInfo)
            return expectedCountryInfo
        }
    }

    private class TestCapitalLocalRepository : CapitalLocalRepository {

        val allCountries = mutableListOf<CountryInfo>()

        var allCountryCalled = 0

        var expectedExistCountry = false

        var saveCountryCalled = 0

        val existedCountryCalled = mutableListOf<Boolean>()

        fun setNewCountryList(list: List<CountryInfo>) {
            allCountries.clear()
            allCountries.addAll(list)
        }

        override suspend fun allCountries(): List<CountryInfo> {
            allCountryCalled++
            return allCountries
        }

        override suspend fun existCountryWithCapital(capital: String): Boolean {
            var result = false
            allCountries.forEach {
                if (it.capital.toLowerCase() == capital) {
                    result = true
                    return@forEach
                }
            }
            existedCountryCalled.add(result)
            return result
        }

        override suspend fun saveCountry(countryInfo: CountryInfo) {
            saveCountryCalled++
            allCountries.addFirst(countryInfo)
        }
    }

    private class TestManageResources : ManageResource {
        override fun string(id: Int) = when (id) {
            R.string.no_connection_message -> "No internet connection"
            R.string.illegal_capital -> "Unavailable capital"
            else -> ""
        }

    }
}
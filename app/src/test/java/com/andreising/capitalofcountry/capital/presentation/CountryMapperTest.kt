package com.andreising.capitalofcountry.capital.presentation

import com.andreising.capitalofcountry.capital.domain.CountryCurrency
import com.andreising.capitalofcountry.capital.domain.CountryInfo
import junit.framework.TestCase.assertEquals
import org.junit.Test


class CapitalResultMapperTest : BaseTest() {
    @Test
    fun `check error`() {
        val communications = TestCapitalCommunications()
        val mapper = CapitalResultMapper(communications, CountryUiMapper())
        mapper.map(emptyList(), "Some error message")
        assertEquals(UiState.Error("Some error message"), communications.checkedStates[0])
    }

    @Test
    fun `check success and empty list`() {
        val communications = TestCapitalCommunications()
        val mapper = CapitalResultMapper(communications, CountryUiMapper())
        mapper.map(emptyList(), "")
        assertEquals(UiState.Success, communications.checkedStates[0])
        assertEquals(0, communications.listCallsCount)
    }

    @Test
    fun `check success and not empty list`() {
        val communications = TestCapitalCommunications()
        val mapper = CapitalResultMapper(communications, CountryUiMapper())
        val countryInfo = CountryInfo(
            "name",
            "capital",
            "region",
            listOf("language1", "language2"),
            "flagLink",
            CountryCurrency("name", "symbol")
        )
        val expectedCountryUiInfo = CountryUi(
            name = "name",
            capital = "capital",
            region = "region",
            languages = "language1, language2",
            flagLink = "flagLink",
            currencyInfo = "name symbol"
        )
        mapper.map(listOf(countryInfo), "")
        assertEquals(UiState.Success, communications.checkedStates[0])
        assertEquals(1, communications.listCallsCount)
        assertEquals(expectedCountryUiInfo, communications.countryList[0])
    }
}
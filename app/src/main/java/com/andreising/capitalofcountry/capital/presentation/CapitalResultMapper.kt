package com.andreising.capitalofcountry.capital.presentation

import com.andreising.capitalofcountry.capital.domain.CapitalResult
import com.andreising.capitalofcountry.capital.domain.CountryInfo

class CapitalResultMapper(
    private val communications: CapitalCommunications,
    private val countryUiMapper: CountryUiMapper
) : CapitalResult.Mapper<Unit> {
    override fun map(
        list: List<CountryInfo>,
        message: String
    ) = communications.showState(
        if (message.isEmpty()) {
            if (list.isNotEmpty()) {
                communications.showList(list.map { countryUiMapper.map(it) })
            }
            UiState.Success
        } else {
            UiState.Error(message)
        }
    )
}
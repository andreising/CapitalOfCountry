package com.andreising.capitalofcountry.capital.presentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

abstract class BaseTest {
    protected class TestCapitalCommunications : CapitalCommunications {

        val checkedProgress = mutableListOf<Boolean>()

        val checkedStates = mutableListOf<UiState>()

        val countryList = mutableListOf<CountryUi>()

        var listCallsCount = 0

        override fun showProgress(show: Boolean) {
            checkedProgress.add(show)
        }

        override fun showState(state: UiState) {
            checkedStates.add(state)
        }

        override fun showList(list: List<CountryUi>) {
            countryList.addAll(list)
            listCallsCount++
        }

        override fun observeProgress(
            owner: LifecycleOwner,
            observer: Observer<Boolean>
        ) {
            throw IllegalArgumentException("should not observe in tests")
        }

        override fun observeState(
            owner: LifecycleOwner,
            observer: Observer<UiState>
        ) {
            throw IllegalArgumentException("should not observe in tests")
        }

        override fun observeCountryUiList(
            owner: LifecycleOwner,
            observer: Observer<List<CountryUi>>
        ) {
            throw IllegalArgumentException("should not observe in tests")
        }
    }

}
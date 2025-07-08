package com.andreising.capitalofcountry.capital.presentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

interface CapitalCommunications : CapitalObservers {
    fun showProgress(show: Boolean)

    fun showState(state: UiState)

    fun showList(list: List<CountryUi>)

    class Base(
        private val progress: ProgressCommunications = ProgressCommunications.Base(),
        private val uiState: UiStateCommunications = UiStateCommunications.Base(),
        private val countryUiList: CountryUiListCommunications
        = CountryUiListCommunications.Base()
    ) : CapitalCommunications {
        override fun showProgress(show: Boolean) = progress.map(show)

        override fun showState(state: UiState) = uiState.map(state)

        override fun showList(list: List<CountryUi>) = countryUiList.map(list)

        override fun observeProgress(
            owner: LifecycleOwner,
            observer: Observer<Boolean>
        ) = progress.observe(owner, observer)

        override fun observeState(
            owner: LifecycleOwner,
            observer: Observer<UiState>
        ) = uiState.observe(owner, observer)

        override fun observeCountryUiList(
            owner: LifecycleOwner,
            observer: Observer<List<CountryUi>>
        ) = countryUiList.observe(owner, observer)
    }
}

interface CapitalObservers {
    fun observeProgress(owner: LifecycleOwner, observer: Observer<Boolean>)

    fun observeState(owner: LifecycleOwner, observer: Observer<UiState>)

    fun observeCountryUiList(owner: LifecycleOwner, observer: Observer<List<CountryUi>>)
}

interface ProgressCommunications : Communication.Mutable<Boolean> {
    class Base : ProgressCommunications, Communication.Post<Boolean>()
}

interface UiStateCommunications : Communication.Mutable<UiState> {
    class Base : UiStateCommunications, Communication.Post<UiState>()
}

interface CountryUiListCommunications : Communication.Mutable<List<CountryUi>> {
    class Base : CountryUiListCommunications, Communication.Post<List<CountryUi>>()
}
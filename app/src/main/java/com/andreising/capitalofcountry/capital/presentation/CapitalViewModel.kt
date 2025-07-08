package com.andreising.capitalofcountry.capital.presentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreising.capitalofcountry.R
import com.andreising.capitalofcountry.capital.domain.CapitalInteractor
import kotlinx.coroutines.launch

class CapitalViewModel(
    private val handler: HandleCapitalRequest,
    private val manageResource: ManageResource,
    private val capitalCommunications: CapitalCommunications,
    private val capitalInteractor: CapitalInteractor
) : ViewModel(), CapitalObservers, CapitalFetch {
    override fun observeProgress(
        owner: LifecycleOwner,
        observer: Observer<Boolean>
    ) = capitalCommunications.observeProgress(owner, observer)

    override fun observeState(
        owner: LifecycleOwner,
        observer: Observer<UiState>
    ) = capitalCommunications.observeState(owner, observer)

    override fun observeCountryUiList(
        owner: LifecycleOwner,
        observer: Observer<List<CountryUi>>
    ) = capitalCommunications.observeCountryUiList(owner, observer)

    override fun initialize(isFirstLaunch: Boolean) {
        if (isFirstLaunch) { handler.handle(viewModelScope) { capitalInteractor.init() } }
    }


    override fun fetchCountryByCapital(capital: String) {
        if (capital.isEmpty()) {
            capitalCommunications.showState(
                UiState.Error(manageResource.string(R.string.empty_number_error_message))
            )
        } else {
            handler.handle(viewModelScope) { capitalInteractor.countryByCapital(capital) }
        }
    }
}

interface CapitalFetch {
    fun initialize(isFirstLaunch: Boolean)
    fun fetchCountryByCapital(capital: String)
}


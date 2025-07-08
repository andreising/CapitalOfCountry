package com.andreising.capitalofcountry.capital.presentation

import com.andreising.capitalofcountry.capital.domain.CapitalInteractor
import com.andreising.capitalofcountry.capital.domain.CapitalResult
import com.andreising.capitalofcountry.capital.domain.CountryCurrency
import com.andreising.capitalofcountry.capital.domain.CountryInfo
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class CapitalViewModelTest : BaseTest() {

    private lateinit var viewModel: CapitalViewModel
    private lateinit var testManageResources: TestManageResources
    private lateinit var interactor: TestCapitalInteractor
    private lateinit var communications: TestCapitalCommunications

    private val mainThreadSurrogate = newSingleThreadContext("Ui thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun init() {
        Dispatchers.setMain(mainThreadSurrogate)
        communications = TestCapitalCommunications()
        interactor = TestCapitalInteractor()
        val mapper = CapitalResultMapper(communications, CountryUiMapper())
        testManageResources = TestManageResources()
        viewModel = CapitalViewModel(
            TestDispatchersList(),
            testManageResources,
            communications,
            interactor,
            mapper
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `#1 standard start`() = runBlocking {

        // 2. action
        viewModel.initialize(isFirstLaunch = true)

        //3. check
        assertEquals(true, communications.checkedProgress.first())

        assertEquals(1, interactor.initCalledList.size)
        assertEquals(CapitalResult.Success(), interactor.initCalledList[0])

        assertEquals(0, communications.listCallsCount)

        assertEquals(1, communications.checkedStates.size)
        assertEquals(UiState.Success, communications.checkedStates[0])

        assertEquals(2, communications.checkedProgress.size)
        assertEquals(false, communications.checkedProgress[1])

    }

    @Test
    fun `#2 start without internet connection`() = runBlocking {

        // 2. action
        interactor.changeReturnedResult(CapitalResult.Failure("No internet connection"))
        viewModel.initialize(isFirstLaunch = true)

        //3. check
        assertEquals(true, communications.checkedProgress.first())

        assertEquals(1, communications.checkedStates.size)
        assertEquals(UiState.Error("No internet connection"), communications.checkedStates[0])

        assertEquals(1, interactor.initCalledList.size)
        assertEquals(CapitalResult.Failure("No internet connection"), interactor.initCalledList[0])

        assertEquals(0, communications.listCallsCount)
    }

    @Test
    fun `#3 standard start, writing capital, fetch data, check success`() = runBlocking {

        // 2. action
        viewModel.initialize(isFirstLaunch = true)

        //3. check
        assertEquals(true, communications.checkedProgress.first())

        assertEquals(2, communications.checkedProgress.size)
        assertEquals(false, communications.checkedProgress[1])

        assertEquals(0, communications.listCallsCount)
        assertEquals(0, communications.countryList.size)

        assertEquals(1, communications.checkedStates.size)
        assertEquals(UiState.Success, communications.checkedStates[0])


        // 3. prepare to success
        val successResult = CapitalResult.Success(
            data = listOf(
                CountryInfo(
                    name = "Germany",
                    capital = "Berlin",
                    // in api it is subregion
                    region = "Western Europe",
                    languages = listOf("German"),
                    flagLink = "https://flagcdn.com/w320/de.png",
                    currency = CountryCurrency(
                        name = "Euro",
                        symbol = "€"
                    )
                )
            )
        )
        interactor.changeReturnedResult(successResult)

        // 5. write real capital and fetch
        viewModel.fetchCountryByCapital("berlin")

        // 6. check states
        assertEquals(true, communications.checkedProgress[2])

        assertEquals(1, interactor.countryCalledList.size)

        assertEquals(2, communications.checkedStates.size)
        assertEquals(UiState.Success, communications.checkedStates[1])

        assertEquals(1, communications.listCallsCount)
        assertEquals(
            listOf(
                CountryUi(
                    name = "Germany",
                    capital = "Berlin",
                    // in api it is subregion
                    region = "Western Europe",
                    languages = "German",
                    flagLink = "https://flagcdn.com/w320/de.png",
                    currencyInfo = "Euro €"
                )
            ), communications.countryList
        )

        assertEquals(4, communications.checkedProgress.size)
        assertEquals(false, communications.checkedProgress[3])

    }

    @Test
    fun `#4 standard start, writing capital, fetch data, check success, re-init, check success`() =
        runBlocking {

            // 2. action
            viewModel.initialize(isFirstLaunch = true)

            //3. check
            assertEquals(true, communications.checkedProgress.first())

            assertEquals(1, communications.checkedStates.size)
            assertEquals(UiState.Success, communications.checkedStates[0])

            assertEquals(0, communications.listCallsCount)
            assertEquals(0, communications.countryList.size)

            assertEquals(2, communications.checkedProgress.size)
            assertEquals(false, communications.checkedProgress[1])

            // 4. prepare to success
            val successResult = CapitalResult.Success(
                data = listOf(
                    CountryInfo(
                        name = "Germany",
                        capital = "Berlin",
                        // in api it is subregion
                        region = "Western Europe",
                        languages = listOf("German"),
                        flagLink = "https://flagcdn.com/w320/de.png",
                        currency = CountryCurrency(
                            name = "Euro",
                            symbol = "€"
                        )
                    )
                )
            )
            interactor.changeReturnedResult(successResult)

            // 5. write real capital and fetch
            viewModel.fetchCountryByCapital("berlin")

            // 6. check states
            assertEquals(true, communications.checkedProgress[2])

            assertEquals(2, communications.checkedStates.size)
            assertEquals(UiState.Success, communications.checkedStates[1])

            assertEquals(1, communications.listCallsCount)
            assertEquals(
                listOf(
                    CountryUi(
                        name = "Germany",
                        capital = "Berlin",
                        // in api it is subregion
                        region = "Western Europe",
                        languages = "German",
                        flagLink = "https://flagcdn.com/w320/de.png",
                        currencyInfo = "Euro €"
                    )
                ), communications.countryList
            )

            assertEquals(4, communications.checkedProgress.size)
            assertEquals(false, communications.checkedProgress[3])

            // 7. re-init
            viewModel.initialize(isFirstLaunch = false)

            // 8. check states
            assertEquals(2, communications.checkedStates.size)
            assertEquals(UiState.Success, communications.checkedStates[1])

            assertEquals(1, communications.listCallsCount)
            assertEquals(
                listOf(
                    CountryUi(
                        name = "Germany",
                        capital = "Berlin",
                        // in api it is subregion
                        region = "Western Europe",
                        languages = "German",
                        flagLink = "https://flagcdn.com/w320/de.png",
                        currencyInfo = "Euro €"
                    )
                ), communications.countryList
            )

            assertEquals(4, communications.checkedProgress.size)
            assertEquals(false, communications.checkedProgress[3])


        }

    @Test
    fun `#5 standard start, writing wrong capital name, fetch data, check fail`() = runBlocking {

        // 2. action
        viewModel.initialize(isFirstLaunch = true)

        //3. check
        assertEquals(true, communications.checkedProgress.first())

        assertEquals(1, communications.checkedStates.size)
        assertEquals(UiState.Success, communications.checkedStates[0])

        assertEquals(0, communications.listCallsCount)
        assertEquals(0, communications.countryList.size)

        assertEquals(2, communications.checkedProgress.size)
        assertEquals(false, communications.checkedProgress[1])

        // 4. prepare to failure
        val successResult = CapitalResult.Failure(message = "Illegal name berlni")
        interactor.changeReturnedResult(successResult)

        // 5. write real capital and fetch
        viewModel.fetchCountryByCapital("berlni")

        // 6. check states
        assertEquals(true, communications.checkedProgress[2])

        assertEquals(2, communications.checkedStates.size)
        assertEquals(UiState.Error("Illegal name berlni"), communications.checkedStates[1])

        assertEquals(0, communications.listCallsCount)

        assertEquals(4, communications.checkedProgress.size)
        assertEquals(false, communications.checkedProgress[3])

    }

    @Test
    fun `#6 `() = runBlocking {

        // 2. action
        viewModel.initialize(isFirstLaunch = true)

        //3. check
        assertEquals(true, communications.checkedProgress.first())

        assertEquals(1, communications.checkedStates.size)
        assertEquals(UiState.Success, communications.checkedStates[0])

        assertEquals(0, communications.listCallsCount)
        assertEquals(0, communications.countryList.size)

        assertEquals(2, communications.checkedProgress.size)
        assertEquals(false, communications.checkedProgress[1])

        // 4. prepare to failure
        val failureResult = CapitalResult.Failure(message = "Incorrect capital name: berlni")
        interactor.changeReturnedResult(failureResult)

        // 5. write real capital and fetch
        viewModel.fetchCountryByCapital("berlni")

        // 6. check states
        assertEquals(true, communications.checkedProgress[2])

        assertEquals(2, communications.checkedStates.size)
        assertEquals(
            UiState.Error("Incorrect capital name: berlni"),
            communications.checkedStates[1]
        )

        assertEquals(0, communications.listCallsCount)

        assertEquals(4, communications.checkedProgress.size)
        assertEquals(false, communications.checkedProgress[3])

        // 7. re-init
        viewModel.initialize(isFirstLaunch = false)

        // 8. check that is nothing changes
        assertEquals(4, communications.checkedProgress.size)
        assertEquals(false, communications.checkedProgress[3])

        assertEquals(2, communications.checkedStates.size)
        assertEquals(
            UiState.Error("Incorrect capital name: berlni"),
            communications.checkedStates[1]
        )

        assertEquals(0, communications.listCallsCount)
    }

    @Test
    fun `#11 try to get data with empty text, expect failure`() = runBlocking {

        // 2. action
        viewModel.initialize(isFirstLaunch = true)

        //3. check
        assertEquals(true, communications.checkedProgress.first())

        assertEquals(1, communications.checkedStates.size)
        assertEquals(UiState.Success, communications.checkedStates[0])

        assertEquals(0, communications.listCallsCount)
        assertEquals(0, communications.countryList.size)

        assertEquals(2, communications.checkedProgress.size)
        assertEquals(false, communications.checkedProgress[1])

        // 4. get data with empty edit text
        testManageResources.setExpectedString("Please enter a capital")
        viewModel.fetchCountryByCapital("")

        // 5. expect error
        assertEquals(2, communications.checkedProgress.size)
        assertEquals(false, communications.checkedProgress[1])

        assertEquals(0, communications.listCallsCount)

        assertEquals(2, communications.checkedStates.size)
        assertEquals(UiState.Error("Please enter a capital"), communications.checkedStates[1])
    }

    private class TestManageResources : ManageResource {
        private var string = "test string"
        override fun string(id: Int) = string

        fun setExpectedString(newString: String) {
            string = newString
        }

    }

    private class TestCapitalInteractor : CapitalInteractor {

        private var result: CapitalResult = CapitalResult.Success()

        val initCalledList = mutableListOf<CapitalResult>()

        val countryCalledList = mutableListOf<CapitalResult>()

        fun changeReturnedResult(result: CapitalResult) {
            this.result = result
        }

        override suspend fun countryByCapital(capital: String): CapitalResult {
            countryCalledList.add(result)
            return result
        }

        override suspend fun init(): CapitalResult {
            initCalledList.add(result)
            return result
        }
    }

    private class TestDispatchersList : CoroutineDispatchers {
        override fun io(): CoroutineDispatcher = TestCoroutineDispatcher()

        override fun ui(): CoroutineDispatcher = TestCoroutineDispatcher()

    }
}
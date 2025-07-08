package com.andreising.capitalofcountry.capital.presentation

import com.andreising.capitalofcountry.capital.domain.CapitalResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface HandleCapitalRequest {
    fun handle(
        scope: CoroutineScope,
        block: suspend () -> CapitalResult
    )

    class Base(
        private val dispatchers: CoroutineDispatchers,
        private val communications: CapitalCommunications,
        private val capitalResultMapper: CapitalResultMapper
    ) : HandleCapitalRequest {
        override fun handle(
            scope: CoroutineScope,
            block: suspend () -> CapitalResult
        ) {
            communications.showProgress(true)
            scope.launch(dispatchers.io()){
                val result = block.invoke()
                communications.showProgress(false)
                result.map(capitalResultMapper)
            }
        }
    }
}
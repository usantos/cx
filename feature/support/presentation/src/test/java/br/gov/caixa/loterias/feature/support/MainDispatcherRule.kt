package br.gov.caixa.loterias.feature.support

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(dispatcher: TestDispatcher = UnconfinedTestDispatcher()) :
    br.gov.caixa.loterias.core.testing.coroutines.MainDispatcherRule(dispatcher)

package br.gov.caixa.loterias.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface CoroutineDispatchers {
    val io: CoroutineDispatcher
    val computation: CoroutineDispatcher
    val main: CoroutineDispatcher
}

object DefaultCoroutineDispatchers : CoroutineDispatchers {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val computation: CoroutineDispatcher = Dispatchers.Default
    override val main: CoroutineDispatcher = Dispatchers.Main
}

data class FixedCoroutineDispatchers(
    override val io: CoroutineDispatcher,
    override val computation: CoroutineDispatcher = io,
    override val main: CoroutineDispatcher = io,
) : CoroutineDispatchers

package br.gov.caixa.loterias.core.common

fun interface Mapper<in Input, out Output> {
    fun map(input: Input): Output
}

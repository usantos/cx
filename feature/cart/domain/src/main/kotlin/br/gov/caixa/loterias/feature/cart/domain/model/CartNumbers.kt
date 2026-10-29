package br.gov.caixa.loterias.feature.cart.domain.model

sealed interface CartNumbers {
    data class Flat(val values: List<Int>) : CartNumbers {
        override fun toString() = "CartNumbers.Flat(redacted)"
    }
    data class Columns(val values: List<List<Int>>) : CartNumbers {
        override fun toString() = "CartNumbers.Columns(redacted)"
    }
}

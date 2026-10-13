package br.gov.caixa.loterias.core.domain.marketplace

import java.math.BigDecimal
import java.util.Collections

/** Pure business rules, independent of Android and transport libraries. */
object MarketplaceFilterRules {
    @JvmStatic
    fun inclusiveRange(minimum: Int, maximum: Int): List<Int> =
        if (minimum <= 0 || maximum < minimum) emptyList()
        else Collections.unmodifiableList((minimum..maximum).toList())

    @JvmStatic
    fun toggleSingle(current: Int?, selected: Int): Int? = when {
        selected <= 0 -> current
        current == selected -> null
        else -> selected
    }

    @JvmStatic
    fun validate(filter: MarketplaceFilter?): ValidationResult {
        if (filter == null) return ValidationResult.invalid("Filtro não informado")
        val minimumValue = monetaryValue(filter.minimumValue)
        val maximumValue = monetaryValue(filter.maximumValue)
        val minimumShares = filter.minimumShares
        val maximumShares = filter.maximumShares
        val message = when {
            minimumValue != null && maximumValue != null && minimumValue > maximumValue ->
                "O valor mínimo não pode ser maior que o máximo"
            minimumShares != null && minimumShares < 2 -> "Quantidade mínima não pode ser menor que 2"
            minimumShares != null && minimumShares > 100 -> "Quantidade mínima não pode ser maior que a máxima"
            maximumShares != null && maximumShares > 100 -> "Quantidade máxima não pode ser maior que 100"
            minimumShares != null && maximumShares != null && minimumShares > maximumShares ->
                "A quantidade mínima de cotas não pode ser maior que a máxima"
            maximumShares != null && maximumShares < 2 -> "Quantidade máxima não pode ser menor que 2"
            filter.wantedNumbers.any { it in filter.unwantedNumbers } -> "Um número não pode estar nas duas listas"
            else -> return ValidationResult.valid()
        }
        return ValidationResult.invalid(message)
    }

    @JvmStatic
    fun toQuery(filter: MarketplaceFilter): Map<String, String> {
        val values = linkedMapOf(
            "quantidadeAposta" to filter.betQuantity,
            "quantidadeDezena" to filter.numberQuantity,
            "qtdMinimaCota" to filter.minimumShares,
            "qtdMaximaCota" to filter.maximumShares,
            "valorMinimoCota" to filter.minimumValue,
            "valorMaximoCota" to filter.maximumValue,
            "numerosEscolhidos" to filter.wantedNumbers.joinToString(";"),
            "numerosNaoEscolhidos" to filter.unwantedNumbers.joinToString(";"),
            "numeroLoterico" to filter.lottery,
            "idModalidade" to filter.modalityId,
            "tipoConcurso" to filter.contestType,
        )
        val query = linkedMapOf<String, String>()
        values.forEach { (key, value) ->
            if (value != null && value.toString().isNotEmpty()) query[key] = value.toString()
        }
        return Collections.unmodifiableMap(query)
    }

    private fun monetaryValue(value: String): BigDecimal? =
        value.replace(".", "").replace(',', '.').toBigDecimalOrNull()

    class ValidationResult private constructor(val isValid: Boolean, val message: String) {
        companion object {
            @JvmStatic fun valid() = ValidationResult(true, "")
            @JvmStatic fun invalid(message: String) = ValidationResult(false, message)
        }
    }
}

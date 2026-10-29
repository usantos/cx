package br.gov.caixa.loterias.feature.marketplace.domain.rule

import br.gov.caixa.loterias.feature.marketplace.domain.model.apimessages.localizedMessage as messageText

import java.util.Collections
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter

/** Regras puras de negócio, independentes de Android e das bibliotecas de transporte. */
object MarketplaceFilterRules {
    @JvmStatic
    fun inclusiveRange(minimum: Int, maximum: Int): List<Int> = if (minimum <= 0 || maximum < minimum) {
        emptyList()
    } else {
        Collections.unmodifiableList((minimum..maximum).toList())
    }

    @JvmStatic
    fun toggleSingle(current: Int?, selected: Int): Int? = when {
        selected <= 0 -> current
        current == selected -> null
        else -> selected
    }

    @JvmStatic
    fun validate(filter: MarketplaceFilter?): ValidationResult {
        if (filter == null) return ValidationResult.invalid(messageText("filtro_nao_informado_d8ea12"))
        val minimumShares = filter.minimumShares
        val maximumShares = filter.maximumShares
        val message = when {
            minimumShares != null && minimumShares < 2 -> messageText("quantidade_minima_nao_pode_ser_m_536325")

            minimumShares != null && minimumShares > 100 -> messageText("quantidade_minima_nao_pode_ser_m_d73775")

            maximumShares != null && maximumShares > 100 -> messageText("quantidade_maxima_nao_pode_ser_m_1d14f1")

            minimumShares != null && maximumShares != null && minimumShares > maximumShares ->
                messageText("a_quantidade_minima_de_cotas_nao_d94b6d")

            maximumShares != null && maximumShares < 2 -> messageText("quantidade_maxima_nao_pode_ser_m_6f7be5")

            filter.wantedNumbers.any { it in filter.unwantedNumbers } -> messageText("um_numero_nao_pode_estar_nas_dua_75bd07")

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
            "tipoConcurso" to filter.contestType
        )
        val query = linkedMapOf<String, String>()
        values.forEach { (key, value) ->
            if (value != null && value.toString().isNotEmpty()) query[key] = value.toString()
        }
        return Collections.unmodifiableMap(query)
    }

    class ValidationResult private constructor(val isValid: Boolean, val message: String) {
        companion object {
            @JvmStatic fun valid() = ValidationResult(true, "")

            @JvmStatic fun invalid(message: String) = ValidationResult(false, message)
        }
    }
}

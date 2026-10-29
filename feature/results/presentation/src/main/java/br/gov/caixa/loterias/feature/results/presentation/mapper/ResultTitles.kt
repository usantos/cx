package br.gov.caixa.loterias.feature.results.presentation.mapper

import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.formatting.localizedMessage as messageText

internal fun resultModalityCode(modality: String): String {
    val normalized = java.text.Normalizer.normalize(modality, java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{M}"), "").lowercase().replace(Regex("[_\\s-]"), "")
    return when (normalized) {
        "super7" -> "supersete"
        "+milionaria" -> "maismilionaria"
        else -> normalized
    }
}

internal fun resultTitle(result: DrawResult, ordinaryName: String): String {
    if (!result.specialContest) return ordinaryName
    return result.specialName.ifBlank {
        when (resultModalityCode(result.modality)) {
            "megasena" -> messageText("mega_da_virada_8d37ad")
            "quina" -> messageText("quina_de_sao_joao_3f2b5c")
            "lotofacil" -> messageText("lotofacil_da_independencia_af8f9c")
            "duplasena" -> messageText("dupla_de_pascoa_80ee17")
            "loteca" -> "Loteca Especial"
            else -> ordinaryName
        }
    }
}

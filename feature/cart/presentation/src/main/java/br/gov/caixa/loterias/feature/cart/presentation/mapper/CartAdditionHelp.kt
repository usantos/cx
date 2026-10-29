package br.gov.caixa.loterias.feature.cart.presentation.mapper

internal fun cartHelpAction(modality: String, onHelp: ((Int, String) -> Unit)?): (() -> Unit)? {
    if (onHelp == null) return null
    val normalized = java.text.Normalizer.normalize(modality, java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{M}"), "").uppercase().replace('-', '_').replace(' ', '_')
    val id = when (normalized) {
        "MEGA_SENA", "MEGA_DA_VIRADA" -> 2
        "QUINA", "QUINA_DE_SAO_JOAO" -> 3
        "SUPER_SETE", "SUPER_7" -> 7
        "LOTOFACIL", "LOTOFACIL_DA_INDEPENDENCIA" -> 8
        "+MILIONARIA", "MAIS_MILIONARIA" -> 9
        "DIA_DE_SORTE" -> 11
        "LOTOGOL" -> 14
        "LOTOMANIA" -> 16
        "DUPLA_SENA", "DUPLA_DE_PASCOA" -> 18
        "LOTECA" -> 19
        "TIMEMANIA" -> 20
        else -> return null
    }
    return { onHelp(id, modality) }
}

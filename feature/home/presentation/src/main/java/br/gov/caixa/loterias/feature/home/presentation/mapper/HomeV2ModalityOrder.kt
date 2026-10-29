package br.gov.caixa.loterias.feature.home.presentation.mapper

object HomeV2ModalityOrder {
    val DEFAULT_ORDER = listOf("MEGA_SENA", "MAIS_MILIONARIA", "LOTECA", "DIA_DE_SORTE", "DUPLA_SENA",
        "LOTOMANIA", "QUINA", "TIMEMANIA", "LOTOFACIL", "SUPER_7")
    fun restore(saved: String?, defaults: List<String>): List<String> {
        val known = saved.orEmpty().split(',').filter { it in defaults }.distinct()
        return known + defaults.filterNot { it in known }
    }
    fun move(order: List<String>, from: String, to: String): List<String> {
        val source = order.indexOf(from)
        val target = order.indexOf(to)
        if (source < 0 || target < 0 || source == target) return order
        return order.toMutableList().apply { add(target, removeAt(source)) }
    }
}

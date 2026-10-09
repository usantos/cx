package br.gov.caixa.loterias.apostas.view.homev2

internal object HomeV2ModalityOrder {
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

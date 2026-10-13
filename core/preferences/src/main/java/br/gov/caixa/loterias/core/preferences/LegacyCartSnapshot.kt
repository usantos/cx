package br.gov.caixa.loterias.core.preferences

/** All calls must use the same SQLite connection, without writes or suspension between calls. */
internal interface LegacyCartTableSource {
    fun dataVersion(): Long
    fun rows(table: String): List<LegacyCartRow>?
}

internal fun readLegacyCartSnapshot(source: LegacyCartTableSource): Map<String, List<LegacyCartRow>> {
    val version = source.dataVersion()
    val snapshot = buildMap {
        for (table in listOf(LEGACY_CART_TABLE, "tb_partida_loteca", "tb_partida_lotogol",
            "tb_equipe", "tb_parametro_equipe", "tb_mes_sorte")) {
            source.rows(table)?.let { put(table, it) }
        }
    }
    check(version == source.dataVersion()) { "Carrinho local alterado durante a leitura" }
    return snapshot
}

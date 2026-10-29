package br.gov.caixa.loterias.feature.cart.data.datasource.local

import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_TABLE

/** Todas as chamadas devem usar a mesma conexão SQLite, sem gravações ou suspensões entre elas. */
internal interface CartTableDataSource {
    fun dataVersion(): Long
    fun rows(table: String): List<CartSnapshotRow>?
}

internal fun readLegacyCartSnapshot(source: CartTableDataSource): Map<String, List<CartSnapshotRow>> {
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

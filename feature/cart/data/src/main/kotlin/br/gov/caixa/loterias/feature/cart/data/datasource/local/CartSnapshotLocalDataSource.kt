package br.gov.caixa.loterias.feature.cart.data.datasource.local

internal typealias CartSnapshotRow = Map<String, String?>
internal fun interface CartSnapshotLocalDataSource { suspend fun read(): Map<String, List<CartSnapshotRow>>
}

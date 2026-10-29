package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.runtime.Composable

/** O registro não possui mapas de provedores inicializados, evitando ciclos de inicialização de classes de arquivo. */
typealias CatalogPreview = @Composable (PreviewState) -> Unit

internal fun preview(id: String, content: CatalogPreview) = id to content

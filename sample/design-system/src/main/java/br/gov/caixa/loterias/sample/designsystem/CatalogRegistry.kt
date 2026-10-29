package br.gov.caixa.loterias.sample.designsystem

import androidx.annotation.StringRes

/** Uma entrada por nome e pacote de composable público; sobrecargas compartilham a página de detalhes. */
data class CatalogEntry(
    val id: String,
    val name: String,
    @param:StringRes val category: Int,
    @param:StringRes val description: Int,
    @param:StringRes val signatures: Int,
    @param:StringRes val snippet: Int,
    val searchTerms: String,
)

object CatalogRegistry {
    val entries: List<CatalogEntry> =
        catalogEntries0 +
            catalogEntries1 +
            catalogEntries2 +
            catalogEntries3 +
            catalogEntries4 +
            catalogEntries5 +
            catalogEntries6 +
            catalogEntries7 +
            catalogEntries8 +
            catalogEntries9 +
            catalogEntries10 +
            catalogEntries11 +
            catalogLayoutEntries

    fun search(query: String): List<CatalogEntry> = entries.filter {
        it.id.contains(query.trim(), ignoreCase = true) ||
            it.searchTerms.contains(query.trim(), ignoreCase = true)
    }
}

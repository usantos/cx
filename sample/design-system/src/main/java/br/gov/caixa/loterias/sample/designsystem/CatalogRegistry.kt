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
            catalogLayoutEntries + catalogPaymentCardEntries

    fun findEntry(id: String?): CatalogEntry? {
        val current = entries.find { it.id == id }
        if (current != null || id == null) return current
        val savedName = id.substringAfterLast('.')
        if (!savedName.startsWith("CaixaDS")) return null
        val currentId = id.dropLast(savedName.length) + "Loterias" + savedName
        return entries.find { it.id == currentId }
    }

    fun groups(query: String, type: CatalogType? = null): List<CatalogGroup> {
        val matches = search(query).groupBy { it.category }
        return CatalogType.entries.filter { type == null || it == type }.mapNotNull { group ->
            matches[group.title]?.let { entries -> CatalogGroup(group, entries.sortedBy { it.name }) }
        }
    }

    fun search(query: String): List<CatalogEntry> = entries.filter {
        it.id.contains(query.trim(), ignoreCase = true) ||
            it.searchTerms.contains(query.trim(), ignoreCase = true)
    }
}

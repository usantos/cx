package br.gov.caixa.loterias.feature.cart.data.repository

import android.content.Context
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.VisitorCartEntry
import br.gov.caixa.loterias.feature.cart.domain.rule.VisitorCartPolicy
import br.gov.caixa.loterias.feature.cart.domain.repository.VisitorCartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import br.gov.caixa.loterias.feature.cart.data.mapper.visitorCartSelection
import br.gov.caixa.loterias.feature.cart.data.mapper.legacyVisitorCart
import br.gov.caixa.loterias.feature.cart.data.datasource.local.VisitorCartGraphStorage
import br.gov.caixa.loterias.feature.cart.data.mapper.decodeLegacyCart
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_TABLE
import br.gov.caixa.loterias.feature.cart.data.mapper.guestCartRows

/** Coordena as regras de domínio de visitantes e as gravações atômicas no grafo de transferência legado. */
class VisitorCartRepositoryImpl internal constructor(private val storage: VisitorCartGraphStorage) : VisitorCartRepository {
    constructor(context: Context, scope: br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope) : this(
        br.gov.caixa.loterias.feature.cart.data.datasource.local.AndroidCartPersistence(context, scope).let {
            VisitorCartGraphStorage(it.document, it::requireReady)
        }
    )
    internal constructor(
        document: br.gov.caixa.loterias.feature.cart.data.datasource.local.SecureCartDocument,
    ) : this(VisitorCartGraphStorage(document))

    override suspend fun current(): ShoppingCart = withContext(Dispatchers.IO) {
        legacyVisitorCart(storage.snapshot())
    }

    override suspend fun add(draft: BetDraft, parameters: BettingParameters): ShoppingCart = withContext(Dispatchers.IO) {
        storage.transaction {
            val game = VisitorCartPolicy.gameFor(draft, parameters)
            val before = tables()
            val existing = decodeLegacyCart(before)
            VisitorCartPolicy.requireCapacity(draft, parameters.maximumCartBets, existing.size)
            val unit = VisitorCartPolicy.unitAmount(draft, game)
            if (VisitorCartPolicy.requiresDuplicateCheck(draft)) {
                val candidate = decodeLegacyCart(guestCartRows(draft, game, unit, 1, 1)).single()
                VisitorCartPolicy.requireNoDuplicate(draft, candidate.visitorCartSelection(), existing.map { it.visitorCartSelection() })
            }
            var id = nextId(existing.maxOfOrNull { it.localId } ?: 0L)
            var teamId = nextTeamId(before)
            repeat(draft.quantity) {
                val graph = guestCartRows(draft, game, unit, id, teamId)
                writeGraph(graph)
                teamId += graph["tb_equipe"].orEmpty().size
                VisitorCartPolicy.mirror(draft)?.let { mirror ->
                    val link = VisitorCartPolicy.mirrorLink(id, game.contestNumber)
                    val mirrorGraph = guestCartRows(mirror, game, unit, id + 1, teamId).toMutableMap()
                    mirrorGraph[LEGACY_CART_TABLE] = mirrorGraph[LEGACY_CART_TABLE]!!.map {
                        it + mapOf("espelho" to "1", "vinculo_espelho" to link.toString())
                    }
                    writeGraph(mirrorGraph)
                    linkMirror(id, link)
                    id++
                }
                id++
            }
            saveLastId(id - 1)
            legacyVisitorCart(decodeLegacyCart(tables()))
        }
    }

    override suspend fun remove(item: CartItem): ShoppingCart = withContext(Dispatchers.IO) {
        storage.transaction {
            val entries = decodeLegacyCart(tables()).map { VisitorCartEntry(it.localId, it.mirrorLink) }
            VisitorCartPolicy.removalIds(item.id, entries).forEach { delete(it) }
            legacyVisitorCart(decodeLegacyCart(tables()))
        }
    }

    override suspend fun clear(): ShoppingCart = withContext(Dispatchers.IO) {
        storage.transaction {
            decodeLegacyCart(tables()).forEach { delete(it.localId) }
            legacyVisitorCart(emptyList())
        }
    }
}

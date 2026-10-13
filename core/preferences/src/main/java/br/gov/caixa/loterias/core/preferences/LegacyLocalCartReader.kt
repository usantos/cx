package br.gov.caixa.loterias.core.preferences

import android.content.Context
import br.gov.caixa.loterias.core.domain.cart.*
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.math.BigDecimal
import java.util.Locale

internal typealias LegacyCartRow = Map<String, String?>
internal fun interface LegacyCartSnapshotStore { fun read(): Map<String, List<LegacyCartRow>> }

/** Preserves the legacy DB graph without opening it writable or invoking its destructive helper. */
class LegacyLocalCartReader internal constructor(private val store: LegacyCartSnapshotStore) : LegacyLocalCartRepository {
    override suspend fun snapshot(): List<LegacyLocalCartBet> = decodeLegacyCart(store.read())

    companion object {
        fun fromContext(context: Context): LegacyLocalCartReader =
            LegacyLocalCartReader(AndroidLegacyCartSnapshotStore(context.applicationContext))
    }
}

internal fun decodeLegacyCart(tables: Map<String, List<LegacyCartRow>>): List<LegacyLocalCartBet> {
    fun parameter(id: Long): LegacyCartTeam? {
        if (id == 0L) return null
        return checkNotNull(tables["tb_parametro_equipe"].orEmpty().singleOrNull { it.long("_id") == id }) {
            "Parâmetro de equipe da aposta local não encontrado"
        }.team("indicadorSelecao")
    }
    fun team(id: Long): LegacyCartMatchTeam {
        val row = checkNotNull(tables["tb_equipe"].orEmpty().singleOrNull { it.long("_id") == id }) {
            "Equipe da aposta local não encontrada"
        }
        return LegacyCartMatchTeam(row.team("indicador_selecao"), parameter(row.long("id_parametro_equipe")), row["placar"], row.flag("vitoria"))
    }
    val rows = tables[LEGACY_CART_TABLE].orEmpty()
    check(rows.map { it.requiredId() }.distinct().size == rows.size) { "Identificação local duplicada" }
    return rows.map { row ->
        val localId = row.requiredId()
        val modality = localModality(checkNotNull(row["modalidade"]))
        val selected = row["numeros_selecionados"].jsonOrNull()?.let { json ->
            check(json.isJsonArray) { "Seleção local inválida" }
            if (modality == LegacyCartModality.SUPER_7) {
                LegacyCartNumbers.Columns(json.asJsonArray.map { it.integers() })
            } else LegacyCartNumbers.Flat(json.integers())
        }
        val month = tables["tb_mes_sorte"].orEmpty().singleOrNull { it.int("numero") == row.int("id_mes_sorte") }
        check(row.int("id_mes_sorte") == 0 || month != null) { "Mês da sorte local não encontrado" }
        LegacyLocalCartBet(
            localId = localId,
            serverId = row.long("id_identificador").takeUnless { it == 0L },
            modality = modality,
            contestType = LegacyCartCode(row["tipo_concurso_valor"], row["tipo_concurso_descricao"]),
            numbers = selected,
            clovers = row["trevos_selecionados"].jsonOrNull()?.integers(),
            quickPickIndicator = LegacyCartCode(row.int("indicador_surpresinha_valor"), row["indicador_surpresinha_descricao"]),
            amount = row["valor"]?.toBigDecimal() ?: BigDecimal.ZERO,
            targetContest = row.int("concurso_alvo"),
            repeatCount = row.int("quantidade_teimosinhas"),
            betCount = row.int("quantidade_apostas"),
            mirrored = row.flag("espelho"),
            generateMirror = row.flag("gera_espelho"),
            initialContest = row.int("concurso_inicial"),
            status = LegacyCartCode(row.long("situacao_valor"), row["situacao_descricao"]),
            replacement = row.flag("troca"),
            effectiveDate = row["data_efetivacao"],
            effectiveTime = row["hora_efetivacao"],
            mirrorLink = row.long("vinculo_espelho"),
            numberCount = row.int("quantidade_numeros"),
            cloverCount = row.int("quantidade_trevos"),
            quickPick = row.flag("surpresinha"),
            quickPickCount = row.int("quantidade_surpresinha"),
            favoriteTeam = parameter(row.long("id_time_coracao")),
            luckyMonth = month?.let { LegacyCartMonth(it.int("numero"), it["nome"], it["abreviacao"]) },
            loteca = tables["tb_partida_loteca"].orEmpty().filter { it.long(LEGACY_CART_PARENT) == localId }.map {
                LegacyCartLotecaMatch(team(it.long("id_equipe_um")), team(it.long("id_equipe_dois")), it.flag("empate"))
            },
            lotogol = tables["tb_partida_lotogol"].orEmpty().filter { it.long(LEGACY_CART_PARENT) == localId }.map {
                LegacyCartLotogolMatch(team(it.long("id_equipe_um")), team(it.long("id_equipe_dois")),
                    it.int("index_placar_equipe_um"), it.int("index_placar_equipe_dois"))
            },
        )
    }
}

private fun LegacyCartRow.requiredId(): Long = checkNotNull(this["_id"]?.toLongOrNull()?.takeIf { it > 0 }) { "Identificação local inválida" }
private fun LegacyCartRow.int(name: String) = this[name]?.toInt() ?: 0
private fun LegacyCartRow.long(name: String) = this[name]?.toLong() ?: 0L
private fun LegacyCartRow.flag(name: String) = int(name) == 1
private fun String?.jsonOrNull(): JsonElement? = takeUnless { it.isNullOrBlank() }?.let(JsonParser::parseString)?.takeUnless { it.isJsonNull }
private fun JsonElement.integers(): List<Int> {
    check(isJsonArray) { "Seleção local inválida" }
    return asJsonArray.map {
        check(it.isJsonPrimitive && it.asJsonPrimitive.isNumber) { "Número local inválido" }
        it.asBigDecimal.intValueExact()
    }
}
private fun LegacyCartRow.team(indicatorColumn: String) = LegacyCartTeam(
    int("numero"), this["nome"], int("numero_pais"), this["pais"], this["sigla_pais"], this["uf"],
    this["nome_class"], flag(indicatorColumn), this["descricao_curta"], this["descricao_longa"],
)

internal fun localModality(value: String): LegacyCartModality = when (value.lowercase(Locale.ROOT)) {
    "mega-sena" -> LegacyCartModality.MEGA_SENA
    "quina" -> LegacyCartModality.QUINA
    "lotofácil" -> LegacyCartModality.LOTOFACIL
    "lotomania" -> LegacyCartModality.LOTOMANIA
    "dupla sena", "dupla-sena" -> LegacyCartModality.DUPLA_SENA
    "dia de sorte" -> LegacyCartModality.DIA_DE_SORTE
    "timemania" -> LegacyCartModality.TIMEMANIA
    "super sete" -> LegacyCartModality.SUPER_7
    "+milionária", "mais milionária", "maismilionária" -> LegacyCartModality.MAIS_MILIONARIA
    "loteca" -> LegacyCartModality.LOTECA
    "lotogol" -> LegacyCartModality.LOTOGOL
    "instantânea" -> LegacyCartModality.INSTANTANEA
    "combo" -> LegacyCartModality.COMBO
    "bolao", "bolão" -> LegacyCartModality.BOLAO
    else -> error("Modalidade local desconhecida")
}

internal const val LEGACY_CART_TABLE = "tb_identificao_de_uma_aposta_das_8_modalidades"
internal const val LEGACY_CART_PARENT = "id_identificao_de_uma_aposta_das_8_modalidades"

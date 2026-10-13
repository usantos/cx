package br.gov.caixa.loterias.core.preferences

import br.gov.caixa.loterias.core.domain.betting.*
import com.google.gson.Gson
import java.math.BigDecimal

/** Raw legacy encoding also consumed by the immutable upload snapshot and fingerprint. */
internal fun guestCartRows(draft: BetDraft, game: LotteryGameParameters, amount: BigDecimal,
    id: Long, firstTeamId: Long): Map<String, List<LegacyCartRow>> {
    val gson = Gson()
    val teams = mutableListOf<LegacyCartRow>()
    val parameters = mutableListOf<LegacyCartRow>()
    var nextTeamId = firstTeamId
    fun team(source: SportsTeam, winner: Boolean = false, score: Int? = null): Long {
        val teamId = nextTeamId++
        val row = mapOf("_id" to teamId.toString(), "numero" to source.number.toString(), "nome" to source.name,
            "numero_pais" to source.countryNumber.toString(), "pais" to source.country, "sigla_pais" to source.countryCode,
            "uf" to source.state, "nome_class" to source.className, "descricao_curta" to source.shortDescription,
            "descricao_longa" to source.longDescription)
        parameters += row + ("indicadorSelecao" to if (source.selectionIndicator) "1" else "0")
        teams += row + mapOf("id_parametro_equipe" to teamId.toString(), "indicador_selecao" to if (source.selectionIndicator) "1" else "0",
            "vitoria" to if (winner) "1" else "0", "placar" to score?.toString())
        return teamId
    }
    val favorite = draft.favoriteTeam?.let { team(it) } ?: 0L
    val loteca = draft.lotecaSelections.mapIndexed { index, selection -> mapOf("_id" to (id * 100 + index).toString(),
        LEGACY_CART_PARENT to id.toString(), "id_equipe_um" to team(selection.match.home, selection.homeWin).toString(),
        "id_equipe_dois" to team(selection.match.away, selection.awayWin).toString(), "empate" to if (selection.draw) "1" else "0") }
    val lotogol = draft.lotogolSelections.mapIndexed { index, selection -> mapOf("_id" to (id * 100 + index).toString(),
        LEGACY_CART_PARENT to id.toString(), "id_equipe_um" to team(selection.match.home, score = selection.homeScore).toString(),
        "id_equipe_dois" to team(selection.match.away, score = selection.awayScore).toString(),
        "index_placar_equipe_um" to selection.homeScore.toString(), "index_placar_equipe_dois" to selection.awayScore.toString()) }
    val modality = when (draft.modalityId) {
        2 -> "mega-sena"; 3 -> "quina"; 7 -> "super sete"; 8 -> "lotofácil"; 9 -> "+milionária"
        11 -> "dia de sorte"; 14 -> "lotogol"; 16 -> "lotomania"; 18 -> "dupla sena"; 19 -> "loteca"; 20 -> "timemania"
        else -> throw IllegalArgumentException("Modalidade indisponível para simulação.")
    }
    val row = mapOf("_id" to id.toString(), "modalidade" to modality, "id_identificador" to "0",
        "tipo_concurso_valor" to if (draft.contestType.equals("ESPECIAL", true)) "E" else "N", "tipo_concurso_descricao" to draft.contestType,
        "numeros_selecionados" to gson.toJson(if (draft.modalityId == 7) draft.superSevenColumns else draft.numbers),
        "trevos_selecionados" to gson.toJson(draft.clovers), "valor" to amount.toPlainString(),
        "concurso_alvo" to game.contestNumber.toString(), "concurso_inicial" to game.contestNumber.toString(),
        "quantidade_teimosinhas" to draft.recurringDraws.toString(), "quantidade_apostas" to "1",
        "quantidade_numeros" to draft.numberQuantity.toString(), "quantidade_trevos" to draft.cloverQuantity.toString(),
        "surpresinha" to if (draft.surprise) "1" else "0", "quantidade_surpresinha" to if (draft.surprise) "1" else "0",
        "indicador_surpresinha_valor" to if (draft.surprise) "1" else "0", "indicador_surpresinha_descricao" to if (draft.surprise) "Surpresinha" else "Não",
        "espelho" to "0", "gera_espelho" to if (draft.generateMirror) "1" else "0", "vinculo_espelho" to "0",
        "id_time_coracao" to favorite.toString(), "id_mes_sorte" to (draft.luckyMonth?.number ?: 0).toString(),
        "situacao_valor" to "0", "situacao_descricao" to "", "troca" to "0")
    return mapOf(LEGACY_CART_TABLE to listOf(row), "tb_parametro_equipe" to parameters, "tb_equipe" to teams,
        "tb_partida_loteca" to loteca, "tb_partida_lotogol" to lotogol,
        "tb_mes_sorte" to draft.luckyMonth?.let { listOf(mapOf("numero" to it.number.toString(), "nome" to it.name, "abreviacao" to it.abbreviation)) }.orEmpty())
}

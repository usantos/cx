package br.gov.caixa.loterias.feature.betting.data.repository

import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotogolMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.data.dto.AddBetRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.AddComboRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.AvailablePoolModalityDto
import br.gov.caixa.loterias.feature.betting.data.dto.BetComboDto
import br.gov.caixa.loterias.feature.betting.data.dto.BetComboEntryDto
import br.gov.caixa.loterias.feature.betting.data.dto.BetComboTypeDto
import br.gov.caixa.loterias.feature.betting.data.dto.BettingParametersDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryContestDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryGameParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryModalityDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryPriceDto
import br.gov.caixa.loterias.feature.betting.data.dto.LuckyMonthParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsMatchParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsShieldTeamDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsTeamParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsTeamShieldDto
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.math.BigDecimal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Assert.assertSame
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingRemoteDataSource
import br.gov.caixa.loterias.feature.betting.data.repository.BettingRepositoryImpl

class BettingRepositoryImplTest {
    private val modalityCodes = listOf(
        2 to "MEGA_SENA", 3 to "QUINA", 7 to "SUPER_7", 8 to "LOTOFACIL",
        9 to "MAIS_MILIONARIA", 11 to "DIA_DE_SORTE", 14 to "LOTOGOL", 16 to "LOTOMANIA", 18 to "DUPLA_SENA",
        19 to "LOTECA", 20 to "TIMEMANIA", 1502 to "INSTANTANEA", 999 to ""
    )

    @Test fun `wire requests retain modality codes contest descriptions explicit counts and Super Seven columns`() = runTest {
        val source = Source()
        val repository = BettingRepositoryImpl(source)
        for ((id, code) in modalityCodes) {
            for ((type, wireType) in listOf("NORMAL" to "1", "eSpEcIaL" to "2", "E" to "1")) {
                val columns = if (id ==
                    7
                ) {
                    listOf(listOf(0, 9, 0), emptyList(), listOf(2), listOf(3), listOf(4), listOf(5), listOf(6))
                } else {
                    emptyList()
                }
                val result = repository.addBet(
                    BetDraft(
                        id, type, listOf(7, 1, 7), listOf(6, 2), 2, true, 3,
                        numberQuantity = 5, cloverQuantity = 4, superSevenColumns = columns,
                        contestNumber = 9999, allowLocalDuplicate = true
                    )
                )
                val numbers = if (id == 7) "[[0,9,0],[],[2],[3],[4],[5],[6]]" else "[7,1,7]"
                val expected = JsonParser.parseString(
                    """{
                    "modalidade":"$code","tipoConcurso":{"valor":"$wireType","descricao":"$type"},
                    "numerosSelecionados":$numbers,"numerosSelecionadosMaisMilionariaTrevo":[6,2],
                    "indicadorSurpresinha":{"valor":2,"descricao":""},"quantidadeTeimosinhas":2,
                    "quantidadeApostas":3,"quantidadeNumeros":5,"quantidadeNumerosMaisMilionariaTrevo":4,"surpresinha":true,"concursoAlvo":9999
                }"""
                )
                assertEquals("$id/$type", expected, Gson().toJsonTree(source.bets.last()))
                assertEquals(42L, result.id)
            }
        }
        assertEquals(39, source.bets.size)
        assertEquals(List(39) { "addBet" }, source.calls)
    }

    @Test fun `sports request keeps full team fields flags string scores month and mirror without shield or legend`() = runTest {
        val home = SportsTeam(101, "Azul", "DF", "AZ", "Azul Futebol", 76, "Brasil", "BR", "Equipe", true, "private-shield")
        val away = SportsTeam(102, "Verde", "SP", "VE", "Verde Futebol", 76, "Brasil", "BR", "Equipe", false)
        val match = SportsMatch(14, home, away, "Legenda fora do pedido", "D")
        val source = Source()
        BettingRepositoryImpl(source).addBet(
            BetDraft(
                19,
                numbers = emptyList(),
                numberQuantity = 0,
                favoriteTeam = home,
                lotecaSelections = listOf(LotecaMatchSelection(match, false, true, true)),
                lotogolSelections = listOf(LotogolMatchSelection(match, 0, 9)),
                luckyMonth = LuckyMonth(12, "Dezembro", "DEZ"),
                generateMirror = true
            )
        )
        val homeFields = """"indicadorSelecao":true,"nome":"Azul","numero":101,"numeroPais":76,
            "descricaoCurta":"AZ","descricaoLonga":"Azul Futebol","pais":"Brasil","siglaPais":"BR","uf":"DF","nomeClass":"Equipe""""
        val awayFields = """"indicadorSelecao":false,"nome":"Verde","numero":102,"numeroPais":76,
            "descricaoCurta":"VE","descricaoLonga":"Verde Futebol","pais":"Brasil","siglaPais":"BR","uf":"SP","nomeClass":"Equipe""""
        val expected = JsonParser.parseString(
            """{
            "modalidade":"LOTECA","tipoConcurso":{"valor":"1","descricao":"NORMAL"},
            "numerosSelecionados":[],"numerosSelecionadosMaisMilionariaTrevo":[],
            "indicadorSurpresinha":{"valor":1,"descricao":""},"quantidadeTeimosinhas":0,
            "quantidadeApostas":1,"quantidadeNumeros":0,"quantidadeNumerosMaisMilionariaTrevo":0,"surpresinha":false,
            "timeDoCoracao":{$homeFields},
            "partidasLoteca":[{"equipe1":{$homeFields,"vitoria":false},"equipe2":{$awayFields,"vitoria":true},"empate":true,"numero":14}],
            "partidasLotogol":[{"equipe1":{$homeFields,"placar":"0"},"equipe2":{$awayFields,"placar":"9"}}],
            "mesDeSorte":{"numero":12,"nome":"Dezembro","abreviacao":"DEZ"},"geraEspelho":true
        }"""
        )
        assertEquals(expected, Gson().toJsonTree(source.bets.single()))
    }

    @Test fun `current contest priority controls shield lookup and duplicate shields keep their last value`() = runTest {
        val home = SportsTeamParameterDto(number = 101, name = "Azul")
        val away = SportsTeamParameterDto(number = 102, name = "Verde")
        val current = LotteryGameParameterDto(contest = LotteryContestDto(modalityCode = "MEGA_SENA", number = 10))
        val sports = LotteryGameParameterDto(
            contest = LotteryContestDto(modalityCode = "LOTECA", number = 11),
            teams = listOf(home, SportsTeamParameterDto()),
            matches = listOf(SportsMatchParameterDto(home = home), SportsMatchParameterDto(14, home, away, "Legenda", "D"))
        )
        val source = Source().apply {
            parametersValue = BettingParametersDto(parameters = listOf(LotteryParameterDto(current, sports)))
            shieldsValue = listOf(
                SportsTeamShieldDto(team = SportsShieldTeamDto(101), imageBase64 = "first"),
                SportsTeamShieldDto(team = SportsShieldTeamDto(101), imageBase64 = "last"),
                SportsTeamShieldDto()
            )
        }
        val repository = BettingRepositoryImpl(source)
        val first = repository.parameters().games.single()
        assertEquals(10, first.contestNumber)
        assertTrue(first.hasNextContest)
        assertEquals(listOf("parameters"), source.calls)
        source.parametersValue = BettingParametersDto(parameters = listOf(LotteryParameterDto(nextGame = sports)))
        val next = repository.parameters().games.single()
        assertEquals(11, next.contestNumber)
        assertTrue(next.hasNextContest)
        assertEquals("last", next.teams[0].shieldBase64)
        assertNull(next.teams[1].shieldBase64)
        assertEquals(0, next.teams[1].number)
        assertEquals(1, next.matches.size)
        assertEquals(14, next.matches.single().number)
        assertEquals("Legenda", next.matches.single().legendDescription)
        assertEquals("D", next.matches.single().legendLetter)
        assertEquals(listOf("parameters", "parameters", "shields"), source.calls)
    }

    @Test fun `optional ordinary shield failures retain the existing fallback`() = runTest {
        for (failure in listOf(java.io.IOException("offline"))) {
            val source = Source().apply {
                parametersValue = BettingParametersDto(
                    parameters = listOf(
                        LotteryParameterDto(
                            game = LotteryGameParameterDto(teams = listOf(SportsTeamParameterDto(number = 101, name = "Azul")))
                        )
                    )
                )
                failures["shields"] = failure
            }
            val result = BettingRepositoryImpl(source).parameters()
            assertEquals("Azul", result.games.single().teams.single().name)
            assertNull(result.games.single().teams.single().shieldBase64)
            assertEquals(listOf("parameters", "shields"), source.calls)
        }
    }

    @Test fun `catalog mapping preserves every code and detailed names while null lists remain empty`() = runTest {
        val source = Source().apply {
            modalitiesValue =
                modalityCodes.map { (id, _) -> LotteryModalityDto(id, "Nome $id", "Especial $id", "Gestor $id") } + LotteryModalityDto()
        }
        val repository = BettingRepositoryImpl(source)
        val mapped = repository.modalities()
        assertEquals(modalityCodes.map { it.second } + "", mapped.map { it.code })
        assertEquals(modalityCodes.map { it.first } + 0, mapped.map { it.id })
        assertEquals("Nome 2", mapped.first().name)
        assertEquals("Especial 2", mapped.first().specialName)
        assertEquals("Gestor 2", mapped.first().managerName)
        assertEquals("", mapped.last().name)
        source.modalitiesValue = null
        source.combosValue = null
        assertTrue(repository.modalities().isEmpty())
        assertTrue(repository.combos().isEmpty())
    }

    @Test fun `combos keep prices entry fields and defaults while cart mutation preserves request values`() = runTest {
        val source = Source().apply {
            combosValue = listOf(
                BetComboDto(
                    BetComboTypeDto(7, "  ", "Descrição", true),
                    listOf(
                        BetComboEntryDto(LotteryModalityDto(20, "Time", "Especial", "Gestor"), "ESPECIAL", 3, 4, 5, 6, 99),
                        BetComboEntryDto()
                    ),
                    BigDecimal("12.3400")
                ),
                BetComboDto()
            )
        }
        val repository = BettingRepositoryImpl(source)
        val combos = repository.combos()
        assertEquals(7, combos.first().id)
        assertEquals("Combo", combos.first().name)
        assertEquals("Descrição", combos.first().description)
        assertTrue(combos.first().new)
        assertEquals("12.3400", combos.first().amount.toPlainString())
        val entry = combos.first().entries.first()
        assertEquals("TIMEMANIA", entry.modality.code)
        assertEquals("Gestor", entry.modality.managerName)
        assertEquals("ESPECIAL", entry.contestType)
        assertEquals(
            listOf(3, 4, 5, 6, 99),
            listOf(entry.betQuantity, entry.recurringDraws, entry.numberQuantity, entry.cloverQuantity, entry.contestNumber)
        )
        assertEquals(0, combos.first().entries.last().modality.id)
        assertEquals("", combos.first().entries.last().modality.code)
        assertEquals(BigDecimal.ZERO, combos.last().amount)
        assertEquals(42L, repository.addCombo(7, 3, true).id)
        assertEquals(AddComboRequestDto(7, 3, true), source.combos.single())
        source.cartValue = null
        assertTrue(runCatching { repository.addCombo(7, 3, false) }.isFailure)
        assertTrue(runCatching { repository.addBet(BetDraft(2, numbers = listOf(1))) }.isFailure)
    }

    @Test fun `parameters preserve identity authority blank datetime flags pools and decimal prices`() = runTest {
        val source = Source().apply {
            parametersValue = BettingParametersDto(
                dailyLimit = BigDecimal("1.2300"),
                parameters = listOf(
                    LotteryParameterDto(
                        game = LotteryGameParameterDto(
                            contest = LotteryContestDto(
                                modalityCode = "mega_sena",
                                number = 9,
                                drawDate = "fallback",
                                drawDateTime = "",
                                open = false
                            ),
                            open = true,
                            prices = listOf(LotteryPriceDto(amount = BigDecimal("0.0000"))),
                            luckyMonths = listOf(
                                LuckyMonthParameterDto(0, "Invalid"),
                                LuckyMonthParameterDto(1, " "),
                                LuckyMonthParameterDto(12, "Dezembro", "DEZ")
                            )
                        )
                    ),
                    LotteryParameterDto(),
                    LotteryParameterDto(game = LotteryGameParameterDto())
                ),
                availablePoolModalities = listOf(
                    AvailablePoolModalityDto("MEGA_SENA", LotteryModalityDto(19), " ", 123, "dia", "hora"),
                    AvailablePoolModalityDto("mega_sena"),
                    AvailablePoolModalityDto()
                )
            )
        }
        val result = BettingRepositoryImpl(source).parameters()
        assertEquals("1.2300", result.dailyLimit.toPlainString())
        assertEquals(BigDecimal.ZERO, result.minimumCartAmount)
        assertEquals(0, result.maximumCartBets)
        assertEquals(2, result.games.size)
        with(result.games.first()) {
            assertEquals(0, modality.id)
            assertEquals("Mega Sena", modality.name)
            assertEquals("mega_sena", modality.code)
            assertEquals("", drawDate)
            assertFalse(open)
            assertEquals("0.0000", prices.single().amount.toPlainString())
            assertEquals(listOf(LuckyMonth(12, "Dezembro", "DEZ")), luckyMonths)
        }
        assertTrue(result.games.last().open)
        assertEquals(listOf(19, 0), result.availablePoolModalities.map { it.modalityId })
        assertEquals(listOf("MEGA_SENA", "mega_sena"), result.availablePoolModalities.map { it.modalityCode })
        assertEquals("NORMAL", result.availablePoolModalities.first().contestType)
        assertEquals(123, result.availablePoolModalities.first().contestNumber)
        assertEquals("dia", result.availablePoolModalities.first().resaleClosingDate)
        assertEquals("hora", result.availablePoolModalities.first().resaleClosingTime)
        assertEquals(listOf("parameters"), source.calls)
    }

    @Test fun `mandatory operations propagate the same failures without retry and absent parameters still fail`() = runTest {
        for (failure in listOf(java.io.IOException("offline"), CancellationException("cancelled"))) {
            for (name in listOf("modalities", "parameters", "combos", "addBet", "addCombo")) {
                val source = Source().apply { failures[name] = failure }
                val repository = BettingRepositoryImpl(source)
                val thrown = runCatching {
                    when (name) {
                        "modalities" -> repository.modalities()
                        "parameters" -> repository.parameters()
                        "combos" -> repository.combos()
                        "addBet" -> repository.addBet(BetDraft(2, numbers = listOf(1)))
                        else -> repository.addCombo(7, 3, false)
                    }
                }.exceptionOrNull()
                assertSame(failure, thrown)
                assertEquals(listOf(name), source.calls)
            }
        }
        val source = Source().apply { parametersValue = null }
        val failure = runCatching { BettingRepositoryImpl(source).parameters() }.exceptionOrNull()
        assertTrue(failure is IllegalArgumentException)
        assertEquals("Parâmetros de apostas sem payload", failure?.message)
        assertEquals(listOf("parameters"), source.calls)
    }

    private class Source : BettingRemoteDataSource {
        val calls = mutableListOf<String>()
        val failures = mutableMapOf<String, Throwable>()
        val bets = mutableListOf<AddBetRequestDto>()
        val combos = mutableListOf<AddComboRequestDto>()
        var modalitiesValue: List<LotteryModalityDto>? = emptyList()
        var parametersValue: BettingParametersDto? = BettingParametersDto()
        var shieldsValue: List<SportsTeamShieldDto>? = emptyList()
        var combosValue: List<BetComboDto>? = emptyList()
        var cartValue: CartDto? = CartDto(id = 42)
        private fun called(name: String) {
            calls += name
            failures[name]?.let { throw it }
        }
        override suspend fun modalities() = modalitiesValue.also { called("modalities") }
        override suspend fun bettingParameters() = parametersValue.also { called("parameters") }
        override suspend fun sportsTeamShields() = shieldsValue.also { called("shields") }
        override suspend fun combos() = combosValue.also { called("combos") }
        override suspend fun addBet(request: AddBetRequestDto): CartDto? {
            called("addBet")
            bets += request
            return cartValue
        }
        override suspend fun addCombo(request: AddComboRequestDto): CartDto? {
            called("addCombo")
            combos += request
            return cartValue
        }
    }
}

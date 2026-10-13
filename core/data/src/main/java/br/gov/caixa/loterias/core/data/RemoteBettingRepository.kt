package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.betting.BetCombo
import br.gov.caixa.loterias.core.domain.betting.BetComboEntry
import br.gov.caixa.loterias.core.domain.betting.BetDraft
import br.gov.caixa.loterias.core.domain.betting.BetPrice
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.BettingRepository
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.betting.LuckyMonth
import br.gov.caixa.loterias.core.domain.betting.SportsMatch
import br.gov.caixa.loterias.core.domain.betting.SportsTeam
import br.gov.caixa.loterias.core.domain.betting.PoolModalityAvailability
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import br.gov.caixa.loterias.core.network.AddBetRequestDto
import br.gov.caixa.loterias.core.network.AddComboRequestDto
import br.gov.caixa.loterias.core.network.BetComboDto
import br.gov.caixa.loterias.core.network.DomainCharacterDto
import br.gov.caixa.loterias.core.network.DomainIntegerDto
import br.gov.caixa.loterias.core.network.LotteryGameParameterDto
import br.gov.caixa.loterias.core.network.LotteryModalityDto
import br.gov.caixa.loterias.core.network.LotecaBetMatchDto
import br.gov.caixa.loterias.core.network.LotogolBetMatchDto
import br.gov.caixa.loterias.core.network.LuckyMonthParameterDto
import br.gov.caixa.loterias.core.network.SportsBetTeamDto
import br.gov.caixa.loterias.core.network.SportsMatchParameterDto
import br.gov.caixa.loterias.core.network.SportsTeamParameterDto
import java.math.BigDecimal
import com.google.gson.Gson

class RemoteBettingRepository(
    private val remote: BettingRemoteDataSource,
) : BettingRepository {
    override suspend fun modalities(): List<LotteryModality> =
        remote.modalities().orEmpty().map { it.toDomain() }

    override suspend fun parameters(): BettingParameters {
        val dto = requireNotNull(remote.bettingParameters()) { "Parâmetros de apostas sem payload" }
        val hasSportsContent = dto.parameters.orEmpty().any { parameter ->
            val game = parameter.game ?: parameter.nextGame
            !game?.teams.isNullOrEmpty() || !game?.matches.isNullOrEmpty()
        }
        val shields = if (hasSportsContent) {
            runCatching { remote.sportsTeamShields().orEmpty() }.getOrDefault(emptyList())
                .associate { (it.team?.id?.toInt() ?: -1) to it.imageBase64 }
        } else {
            emptyMap()
        }
        return BettingParameters(
            dailyLimit = dto.dailyLimit ?: BigDecimal.ZERO,
            minimumCartAmount = dto.minimumCartAmount ?: BigDecimal.ZERO,
            maximumCartBets = dto.maximumCartBets ?: 0,
            games = dto.parameters.orEmpty().mapNotNull { parameter ->
                (parameter.game ?: parameter.nextGame)?.toDomain(shields)?.copy(hasNextContest = parameter.nextGame != null)
            },
            availablePoolModalities = dto.availablePoolModalities.orEmpty().map { available ->
                PoolModalityAvailability(
                    modalityId = available.modality?.id ?: modalityId(available.modalityCode),
                    modalityCode = available.modalityCode.orEmpty(),
                    contestType = available.contestType.orEmpty().ifBlank { "NORMAL" },
                    contestNumber = available.contestNumber,
                    resaleClosingDate = available.resaleClosingDate.orEmpty(),
                    resaleClosingTime = available.resaleClosingTime.orEmpty(),
                )
            }.filter { it.modalityId > 0 || it.modalityCode.isNotBlank() },
        )
    }

    override suspend fun combos(): List<BetCombo> = remote.combos().orEmpty().map { it.toDomain() }

    override suspend fun addBet(draft: BetDraft): ShoppingCart {
        val code = modalityCode(draft.modalityId)
        val isSpecial = draft.contestType.equals("ESPECIAL", ignoreCase = true)
        return remote.addBet(
            AddBetRequestDto(
                modality = code,
                contestType = DomainCharacterDto(if (isSpecial) "E" else "N", draft.contestType),
                selectedNumbers = Gson().toJsonTree(
                    if (draft.superSevenColumns.isNotEmpty()) draft.superSevenColumns else draft.numbers,
                ),
                selectedClovers = draft.clovers,
                surpriseIndicator = DomainIntegerDto(if (draft.surprise) 1 else 0, if (draft.surprise) "Surpresinha" else "Não"),
                recurringDraws = draft.recurringDraws,
                quantity = draft.quantity,
                numberQuantity = draft.numberQuantity,
                cloverQuantity = draft.cloverQuantity,
                surprise = draft.surprise,
                favoriteTeam = draft.favoriteTeam?.toRequest(),
                lotecaMatches = draft.lotecaSelections.map { selection ->
                    LotecaBetMatchDto(
                        home = selection.match.home.toRequest(win = selection.homeWin),
                        away = selection.match.away.toRequest(win = selection.awayWin),
                        draw = selection.draw,
                        number = selection.match.number,
                    )
                }.takeIf { it.isNotEmpty() },
                lotogolMatches = draft.lotogolSelections.map { selection ->
                    LotogolBetMatchDto(
                        home = selection.match.home.toRequest(score = selection.homeScore.toString()),
                        away = selection.match.away.toRequest(score = selection.awayScore.toString()),
                    )
                }.takeIf { it.isNotEmpty() },
                luckyMonth = draft.luckyMonth?.toRequest(),
                generateMirror = draft.generateMirror.takeIf { it },
            ),
        ).toDomainCart()
    }

    override suspend fun addCombo(comboId: Int, quantity: Int, surprise: Boolean): ShoppingCart =
        remote.addCombo(AddComboRequestDto(comboId, quantity, surprise)).toDomainCart()

    private fun LotteryModalityDto.toDomain() = LotteryModality(
        id = id ?: 0,
        name = name.orEmpty(),
        code = modalityCode(id ?: 0),
        specialName = specialName.orEmpty(),
        managerName = managerName.orEmpty(),
    )

    private fun LotteryGameParameterDto.toDomain(shields: Map<Int, String?>): LotteryGameParameters {
        val detailed = contest?.modality
        val modality = detailed?.toDomain() ?: LotteryModality(
            id = modalityId(contest?.modalityCode),
            name = contest?.modalityCode.displayModalityName(),
            code = contest?.modalityCode.orEmpty(),
        )
        return LotteryGameParameters(
            modality = modality,
            contestNumber = contest?.number ?: 0,
            drawDate = contest?.drawDateTime ?: contest?.drawDate.orEmpty(),
            maximumNumber = maximumNumber ?: 0,
            minimumSelection = minimumSelection ?: 0,
            maximumSelection = maximumSelection ?: 0,
            maximumSurprises = maximumSurprises ?: 0,
            recurringOptions = recurringOptions.orEmpty(),
            prices = prices.orEmpty().map { price ->
                BetPrice(
                    numberQuantity = price.numberQuantity ?: 0,
                    cloverQuantity = price.cloverQuantity ?: 0,
                    amount = price.amount ?: BigDecimal.ZERO,
                    doubleQuantity = price.doubleQuantity ?: 0,
                    tripleQuantity = price.tripleQuantity ?: 0,
                )
            },
            minimumClovers = clovers?.minimumSelection ?: 0,
            maximumClovers = clovers?.maximumSelection ?: 0,
            maximumCloverNumber = clovers?.maximumNumber ?: 0,
            open = open != false && contest?.open != false,
            contestType = contest?.contestType.orEmpty().ifBlank { "NORMAL" },
            estimatedPrize = contest?.estimatedPrize ?: BigDecimal.ZERO,
            closingDateTime = contest?.closingDateTime.orEmpty(),
            teams = teams.orEmpty().map { it.toDomain(shields) },
            matches = matches.orEmpty().mapNotNull { it.toDomain(shields) },
            luckyMonths = luckyMonths.orEmpty().map { month ->
                LuckyMonth(
                    number = month.number ?: 0,
                    name = month.name.orEmpty(),
                    abbreviation = month.abbreviation.orEmpty(),
                )
            }.filter { it.number > 0 && it.name.isNotBlank() },
            acceptsMirror = acceptsMirror == true,
        )
    }

    private fun LuckyMonth.toRequest() = LuckyMonthParameterDto(
        number = number,
        name = name,
        abbreviation = abbreviation,
    )

    private fun SportsTeamParameterDto.toDomain(shields: Map<Int, String?>) = SportsTeam(
        number = number ?: 0,
        name = name.orEmpty(),
        state = state.orEmpty(),
        shortDescription = shortDescription.orEmpty(),
        longDescription = longDescription.orEmpty(),
        countryNumber = countryNumber ?: 0,
        country = country.orEmpty(),
        countryCode = countryCode.orEmpty(),
        className = className.orEmpty(),
        selectionIndicator = selectionIndicator == true,
        shieldBase64 = shields[number],
    )

    private fun SportsMatchParameterDto.toDomain(shields: Map<Int, String?>): SportsMatch? {
        val homeTeam = home?.toDomain(shields) ?: return null
        val awayTeam = away?.toDomain(shields) ?: return null
        return SportsMatch(
            number = number ?: 0,
            home = homeTeam,
            away = awayTeam,
            legendDescription = legendDescription.orEmpty(),
            legendLetter = legendLetter.orEmpty(),
        )
    }

    private fun SportsTeam.toRequest(win: Boolean? = null, score: String? = null) = SportsBetTeamDto(
        selectionIndicator = selectionIndicator,
        name = name,
        number = number,
        countryNumber = countryNumber,
        shortDescription = shortDescription,
        longDescription = longDescription,
        country = country,
        countryCode = countryCode,
        state = state,
        className = className,
        win = win,
        score = score,
    )

    private fun BetComboDto.toDomain() = BetCombo(
        id = type?.id ?: 0,
        name = type?.name.orEmpty().ifBlank { "Combo" },
        description = type?.description.orEmpty(),
        new = type?.isNew == true,
        amount = amount ?: BigDecimal.ZERO,
        entries = entries.orEmpty().map { entry ->
            BetComboEntry(
                modality = entry.modality?.toDomain() ?: LotteryModality(0, "", ""),
                contestType = entry.contestType.orEmpty(),
                betQuantity = entry.betQuantity ?: 0,
                recurringDraws = entry.recurringDraws ?: 0,
                numberQuantity = entry.numberQuantity ?: 0,
                cloverQuantity = entry.cloverQuantity ?: 0,
                contestNumber = entry.contestNumber ?: 0,
            )
        },
    )

    private fun modalityCode(id: Int) = when (id) {
        2 -> "MEGA_SENA"
        3 -> "QUINA"
        7 -> "SUPER_7"
        8 -> "LOTOFACIL"
        9 -> "MAIS_MILIONARIA"
        11 -> "DIA_DE_SORTE"
        14 -> "LOTOGOL"
        16 -> "LOTOMANIA"
        18 -> "DUPLA_SENA"
        19 -> "LOTECA"
        20 -> "TIMEMANIA"
        1502 -> "INSTANTANEA"
        else -> ""
    }

    private fun modalityId(code: String?) = when (code) {
        "MEGA_SENA" -> 2
        "QUINA" -> 3
        "SUPER_7" -> 7
        "LOTOFACIL" -> 8
        "MAIS_MILIONARIA" -> 9
        "DIA_DE_SORTE" -> 11
        "LOTOGOL" -> 14
        "LOTOMANIA" -> 16
        "DUPLA_SENA" -> 18
        "LOTECA" -> 19
        "TIMEMANIA" -> 20
        "INSTANTANEA" -> 1502
        else -> 0
    }

    private fun String?.displayModalityName() = orEmpty().lowercase().split('_')
        .joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
}

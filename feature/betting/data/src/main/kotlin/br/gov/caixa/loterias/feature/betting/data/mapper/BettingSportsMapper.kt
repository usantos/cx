package br.gov.caixa.loterias.feature.betting.data.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.data.dto.SportsBetTeamDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsMatchParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsTeamParameterDto

internal fun SportsTeamParameterDto.toBettingTeam(shields: Map<Int, String?>) = SportsTeam(
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

internal fun SportsMatchParameterDto.toBettingMatch(shields: Map<Int, String?>): SportsMatch? {
    val homeTeam = home?.toBettingTeam(shields) ?: return null
    val awayTeam = away?.toBettingTeam(shields) ?: return null
    return SportsMatch(
        number = number ?: 0,
        home = homeTeam,
        away = awayTeam,
        legendDescription = legendDescription.orEmpty(),
        legendLetter = legendLetter.orEmpty(),
    )
}

internal fun SportsTeam.toBettingTeamRequest(win: Boolean? = null, score: String? = null) = SportsBetTeamDto(
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

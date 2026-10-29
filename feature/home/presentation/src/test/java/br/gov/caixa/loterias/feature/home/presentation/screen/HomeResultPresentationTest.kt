package br.gov.caixa.loterias.feature.home.presentation.screen

import br.gov.caixa.loterias.feature.results.domain.model.DrawPayout
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeResultPresentationTest {
    @Test fun `legacy number grid changes at seven except Mais Milionaria`() {
        assertEquals(3, homeResultColumns("megasena", 6))
        assertEquals(4, homeResultColumns("lotofacil", 15))
        assertEquals(3, homeResultColumns("+Milionária", 7))
        assertEquals("supersete", normalizeModality("SUPER_7"))
    }

    @Test fun `band codes are readable for all legacy modality families`() {
        assertEquals("Sena - 6 números acertados", homeResultPayoutDescription("MEGA_SENA", "0001"))
        assertEquals("Terno - 3 números acertados", homeResultPayoutDescription("duplasena", "0008"))
        assertEquals("0 acertos", homeResultPayoutDescription("lotomania", "0007"))
        assertEquals("2 números e 1 trevo acertados", homeResultPayoutDescription("maismilionaria", "00010"))
        assertEquals("Time do Coração", homeResultPayoutDescription("timemania", "0006"))
        assertEquals("Mês de sorte", homeResultPayoutDescription("diadesorte", "0005"))
        assertEquals("2º (4 acertos)", homeResultPayoutDescription("lotogol", "0002"))
        assertEquals("Faixa especial", homeResultPayoutDescription("quina", "Faixa especial"))
    }

    @Test fun `winner wording distinguishes zero singular and plural`() {
        assertEquals("Sem ganhadores", homeResultWinners(0))
        assertEquals("1 aposta ganhadora", homeResultWinners(1))
        assertEquals("12 apostas ganhadoras", homeResultWinners(12))
    }

    @Test fun `legacy combined Dupla Sena bands split into both draws without dropping bands`() {
        val bands = (1..8).map { DrawPayout("$it", it.toLong(), "100") }
        val result = result("duplasena", bands)
        assertEquals(bands.take(4), homeResultPayouts(result).first)
        assertEquals(bands.drop(4), homeResultPayouts(result).second)
        assertEquals(bands, homeResultPayouts(result("megasena", bands)).first)
        val explicit = result.copy(payouts = bands.take(4), secondDrawPayouts = bands.drop(4))
        assertEquals(bands.take(4) to bands.drop(4), homeResultPayouts(explicit))
    }

    @Test fun `date title capitalizes month and keeps numeric server dates intact`() {
        assertEquals("21 Outubro 2026", homeResultDate("21 outubro 2026"))
        assertEquals("21/10/2026", homeResultDate("21/10/2026"))
    }

    @Test fun `sports region follows legacy country and Brazil club rules`() {
        val club = br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam("Clube", "SP", countryCode = "BRA")
        assertEquals("/SP", homeSportsTeamRegion(club))
        assertEquals("/ARG", homeSportsTeamRegion(club.copy(countryCode = "ARG")))
        assertEquals("/BRA", homeSportsTeamRegion(club.copy(name = "BRASIL")))
        assertEquals("", homeSportsTeamRegion(club.copy(countryCode = "")))
    }

    private fun result(modality: String, payouts: List<DrawPayout>) = DrawResult(
        modality, 1, "", "", emptyList(), emptyList(), emptyList(), false, "", "", "", payouts,
    )
}

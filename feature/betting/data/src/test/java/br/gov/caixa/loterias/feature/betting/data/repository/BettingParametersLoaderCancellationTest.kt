package br.gov.caixa.loterias.feature.betting.data.repository

import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.data.dto.AddBetRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.AddComboRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.BetComboDto
import br.gov.caixa.loterias.feature.betting.data.dto.BettingParametersDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryGameParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryModalityDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsTeamParameterDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsTeamShieldDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingRemoteDataSource
import br.gov.caixa.loterias.feature.betting.data.repository.BettingRepositoryImpl

class BettingParametersLoaderCancellationTest {
    @Test fun `direct shields cancellation propagates unchanged without mapping parameters`() = runTest {
        val cancellation = CancellationException("shields cancelled")
        val source = Source { throw cancellation }

        val thrown = runCatching { BettingRepositoryImpl(source).parameters() }.exceptionOrNull()

        assertSame(cancellation, thrown)
        assertEquals("Cancellation must stop before the domain mapper reads parameters", 1, source.parameterReads)
    }

    @Test fun `cancelling suspended shields does not map or publish successful parameters`() = runTest {
        val shieldsStarted = CompletableDeferred<Unit>()
        val source = Source {
            shieldsStarted.complete(Unit)
            awaitCancellation()
        }
        val published = mutableListOf<BettingParameters>()
        val loading = launch {
            published += BettingRepositoryImpl(source).parameters()
        }
        shieldsStarted.await()

        loading.cancelAndJoin()

        assertTrue(loading.isCancelled)
        assertTrue("Cancelled loading must not publish empty-shield success", published.isEmpty())
        assertEquals("Cancelled loading must stop before domain mapping", 1, source.parameterReads)
    }

    @Test fun `ordinary shields failure still returns sports parameters with empty shields`() = runTest {
        val source = Source { throw java.io.IOException("offline") }

        val parameters = BettingRepositoryImpl(source).parameters()

        assertEquals("Azul", parameters.games.single().teams.single().name)
        assertNull(parameters.games.single().teams.single().shieldBase64)
    }

    private class Source(private val shields: suspend () -> List<SportsTeamShieldDto>?) : BettingRemoteDataSource {
        var parameterReads = 0
        private val parameter = LotteryParameterDto(
            game = LotteryGameParameterDto(
                teams = listOf(SportsTeamParameterDto(number = 101, name = "Azul"))
            )
        )

        // Uma leitura descobre conteúdo esportivo; uma leitura posterior entraria no mapeamento de domínio.
        private val parameters = object : AbstractList<LotteryParameterDto>() {
            override val size = 1
            override fun get(index: Int): LotteryParameterDto {
                check(index == 0)
                parameterReads++
                return parameter
            }
        }

        override suspend fun bettingParameters() = BettingParametersDto(parameters = parameters)
        override suspend fun sportsTeamShields() = shields()
        override suspend fun modalities(): List<LotteryModalityDto>? = error("Unexpected modalities request")
        override suspend fun combos(): List<BetComboDto>? = error("Unexpected combos request")
        override suspend fun addBet(request: AddBetRequestDto): CartDto? = error("Unexpected bet submission")
        override suspend fun addCombo(request: AddComboRequestDto): CartDto? = error("Unexpected combo submission")
    }
}

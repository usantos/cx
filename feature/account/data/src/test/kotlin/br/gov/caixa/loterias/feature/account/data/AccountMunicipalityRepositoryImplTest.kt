package br.gov.caixa.loterias.feature.account.data

import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSource
import br.gov.caixa.loterias.feature.account.data.dto.MunicipalityIdDto
import br.gov.caixa.loterias.feature.account.data.dto.UserProfileDto
import br.gov.caixa.loterias.feature.account.data.repository.AccountMunicipalityRepositoryImpl
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountMunicipalityRepositoryImplTest {
    @Test fun `consulta somente o perfil e preserva municipio e estado recebidos`() = runTest {
        val source = Source { UserProfileDto(municipalityId = MunicipalityIdDto(STATE_ID, MUNICIPALITY_NUMBER, CHECK_DIGIT)) }
        assertEquals(MunicipalityId(STATE_ID, MUNICIPALITY_NUMBER, CHECK_DIGIT), AccountMunicipalityRepositoryImpl(source).municipality())
        assertEquals(1, source.profileReads)
        assertEquals(0, source.municipalityReads)
    }

    @Test fun `campos ausentes do identificador conservam fallback zero`() = runTest {
        val source = Source { UserProfileDto(municipalityId = MunicipalityIdDto()) }
        assertEquals(MunicipalityId(0, 0, 0), AccountMunicipalityRepositoryImpl(source).municipality())
        assertEquals(1, source.profileReads)
        assertEquals(0, source.municipalityReads)
    }

    @Test fun `perfil ausente continua rejeitado sem consultar municipios`() = runTest {
        val source = Source { null }
        assertTrue(runCatching { AccountMunicipalityRepositoryImpl(source).municipality() }.exceptionOrNull() is IllegalArgumentException)
        assertEquals(1, source.profileReads)
        assertEquals(0, source.municipalityReads)
    }

    @Test fun `identificador de municipio ausente continua rejeitado`() = runTest {
        val source = Source { UserProfileDto() }
        assertTrue(runCatching { AccountMunicipalityRepositoryImpl(source).municipality() }.exceptionOrNull() is IllegalArgumentException)
        assertEquals(0, source.municipalityReads)
    }

    @Test fun `falha remota conserva a mesma causa`() = runTest {
        val failure = IOException("indisponivel")
        assertSame(failure, runCatching { AccountMunicipalityRepositoryImpl(Source { throw failure }).municipality() }.exceptionOrNull())
    }

    @Test fun `cancelamento continua cancelamento`() = runTest {
        val cancellation = CancellationException("cancelado")
        assertSame(
            cancellation,
            runCatching {
                AccountMunicipalityRepositoryImpl(Source { throw cancellation }).municipality()
            }.exceptionOrNull()
        )
    }

    private class Source(private val read: suspend () -> UserProfileDto?) : AccountRemoteDataSource {
        var profileReads = 0
        var municipalityReads = 0
        override suspend fun currentUserProfile(): UserProfileDto? {
            profileReads++
            return read()
        }
        override suspend fun municipalities(stateId: Long): List<br.gov.caixa.loterias.feature.account.data.dto.MunicipalityDto>? {
            municipalityReads++
            error("A localizacao do Marketplace nao consulta a lista de municipios")
        }
        override suspend fun neighborhoodsByZipCode(
            zipCode: String,
        ): List<br.gov.caixa.loterias.feature.account.data.dto.NeighborhoodDto>? = error("Nao utilizado")
        override suspend fun updatePersonalData(request: br.gov.caixa.loterias.feature.account.data.dto.PersonalDataUpdateDto) =
            error("Nao utilizado")
        override suspend fun registerAccount(request: br.gov.caixa.loterias.feature.account.data.dto.AccountRegistrationDto) =
            error("Nao utilizado")
        override suspend fun verifyAccount(): br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationRemoteData =
            error("Nao utilizado")
        override suspend fun recalculateAuthorizedLimit(): UserProfileDto? = error("Nao utilizado")
    }
    private companion object {
        const val STATE_ID = 7L
        const val MUNICIPALITY_NUMBER = 123L
        const val CHECK_DIGIT = 4L
    }
}

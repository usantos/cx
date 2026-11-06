package br.gov.caixa.loterias.core.domain.configuration

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.repository.VersionSupportRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.IsVersionSupported
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class IsVersionSupportedTest {
    @Test
    fun `checks the requested version using only the version support contract`() = runTest {
        val repository = VersionSupportRepository { version -> version == "1.24.0" }
        val isVersionSupported = IsVersionSupported(repository)

        assertEquals(AppResult.Success(true), isVersionSupported("1.24.0"))
        assertEquals(AppResult.Success(false), isVersionSupported("1.23.0"))
    }
}

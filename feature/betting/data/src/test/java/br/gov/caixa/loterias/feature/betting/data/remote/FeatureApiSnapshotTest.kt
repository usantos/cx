package br.gov.caixa.loterias.feature.betting.data.remote

import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.feature.betting.data.dto.RapidConfigurationDto
import java.lang.reflect.Proxy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.featureBettingApi

class FeatureApiSnapshotTest {
    @Test fun clientRotationDuringArgumentCreationDoesNotRebindTheSelectedOperation() = runTest {
        val calls = mutableListOf<String>()
        fun backend(name: String) = Proxy.newProxyInstance(
            BackendApi::class.java.classLoader, arrayOf(BackendApi::class.java),
        ) { _, method, arguments ->
            check(method.name == "saveRapidConfiguration")
            calls += name
            ApiEnvelope(arguments[0] as RapidConfigurationDto)
        } as BackendApi
        val provider = BackendApiProvider()
        provider.replace(backend("original"))
        fun requestAfterRotation(): RapidConfigurationDto {
            provider.replace(backend("rotated"))
            return RapidConfigurationDto()
        }

        provider.current().featureBettingApi().saveRapidConfiguration(requestAfterRotation())
        provider.current().featureBettingApi().saveRapidConfiguration(RapidConfigurationDto())

        assertEquals(listOf("original", "rotated"), calls)
    }
}

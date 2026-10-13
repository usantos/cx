package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.network.AppParametersMapDto
import com.google.gson.Gson
import java.lang.reflect.Proxy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class PoolSalesConfigurationTest {
    @Test fun `history menu uses consultaMinhasApostas with legacy enabled fallback`() = runTest {
        listOf(
            """{"consultaMinhasApostas":true}""" to true,
            """{"consultaMinhasApostas":false}""" to false,
            """{"consultaMinhasApostas":null}""" to true,
            """{"habilitaMenuApostas":false}""" to true,
            "{}" to true,
            "null" to true,
        ).forEach { (parameters, expected) ->
            val dto = Gson().fromJson("{\"mapa\":$parameters}", AppParametersMapDto::class.java)
            val source = Proxy.newProxyInstance(AppConfigurationRemoteDataSource::class.java.classLoader,
                arrayOf(AppConfigurationRemoteDataSource::class.java)) { _, method, _ ->
                check(method.name == "appParameters")
                dto
            } as AppConfigurationRemoteDataSource
            assertEquals(parameters, expected, RemoteAppConfigurationRepository(source).homeFeatures().myBetsVisible)
        }
    }

    @Test fun `legacy local preference is fed by resale wire flag with disabled fallback`() = runTest {
        listOf(
            """{"habilitaRevendaBolaoApp":true}""" to true,
            """{"habilitaRevendaBolaoApp":false}""" to false,
            """{"isBolaoHabilitado":true}""" to false,
            "{}" to false,
        ).forEach { (parameters, expected) ->
            val dto = Gson().fromJson("{\"mapa\":$parameters}", AppParametersMapDto::class.java)
            var requests = 0
            val source = Proxy.newProxyInstance(AppConfigurationRemoteDataSource::class.java.classLoader,
                arrayOf(AppConfigurationRemoteDataSource::class.java)) { _, method, _ ->
                check(method.name == "appParameters")
                requests++
                dto
            } as AppConfigurationRemoteDataSource
            val repository = RemoteAppConfigurationRepository(source)
            assertEquals(expected, repository.homeFeatures().poolSalesEnabled)
            assertEquals(expected, repository.homeFeatures().poolSalesEnabled)
            assertEquals(1, requests)
        }
    }
}

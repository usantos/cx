package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSource
import br.gov.caixa.loterias.core.data.repository.AppConfigurationRepositoryImpl
import br.gov.caixa.loterias.core.network.model.AppParametersMapDto
import com.google.gson.Gson
import java.lang.reflect.Proxy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class HomeSpecialContestsConfigurationTest {
    @Test fun `exact legacy serialized names reach Home configuration and absent values remain absent`() = runTest {
        listOf(
            """{"concursoMega30Anos":3000,"concursoLotecaPais":1234}""" to (3000 to 1234),
            """{"concursoMega30Anos":3001}""" to (3001 to null),
            "{}" to (null to null),
            "null" to (null to null),
        ).forEach { (json, expected) ->
            val dto = Gson().fromJson("{\"mapa\":$json}", AppParametersMapDto::class.java)
            val source = Proxy.newProxyInstance(AppConfigurationRemoteDataSource::class.java.classLoader,
                arrayOf(AppConfigurationRemoteDataSource::class.java)) { _, method, _ ->
                check(method.name == "appParameters"); dto
            } as AppConfigurationRemoteDataSource
            val actual = AppConfigurationRepositoryImpl(source).homeFeatures().specialContests
            assertEquals(expected.first, actual.megaThirtyYearsContest)
            assertEquals(expected.second, actual.lotecaFathersDayContest)
        }
    }
}

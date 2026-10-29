package br.gov.caixa.loterias.feature.betting.data.mapper

import br.gov.caixa.loterias.feature.betting.data.dto.BettingParametersDto
import com.google.gson.Gson
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PoolAvailabilityMetadataTest {
    @Test fun `original pool metadata keys survive transport and domain mapping`() {
        val dto = Gson().fromJson(
            """{"modalidadesDisponiveisCotas":[{
            "modalidade":"MEGA_SENA","tipoConcurso":"ESPECIAL","concurso":3000,
            "modalidadeDetalhada":{"valor":2,"descricao":"Mega-Sena","descricaoEspecial":"Mega da Virada"},
            "estimativa":200000000,"dataHoraSorteio":"31/12/2026 22:00:00"
        }]}""",
            BettingParametersDto::class.java
        )
        val pool = dto.toBettingParameters(emptyMap()).availablePoolModalities.single()
        assertEquals("Mega da Virada", pool.modality?.specialName)
        assertEquals(BigDecimal("200000000"), pool.estimatedPrize)
        assertEquals("31/12/2026 22:00:00", pool.drawDateTime)
        val missing = Gson().fromJson("""{"modalidadesDisponiveisCotas":[{"modalidade":"QUINA"}]}""", BettingParametersDto::class.java)
            .toBettingParameters(emptyMap()).availablePoolModalities.single()
        assertNull(missing.modality)
        assertNull(missing.estimatedPrize)
        assertEquals("", missing.drawDateTime)
    }
}

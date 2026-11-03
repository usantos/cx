package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.feature.retail.presentation.mapper.locationActionLabel
import br.gov.caixa.loterias.feature.retail.presentation.mapper.locationMessage
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LocationPresentationRulesTest {
    @Test fun `legacy messages and actions cover provider permission territory and failure`() {
        assertEquals(
            "Precisamos conferir se você está em território brasileiro.\nHabilite sua localização.",
            locationMessage(LocationGateStatus.PERMISSION_REQUIRED)
        )
        assertEquals("Ir para configurações", locationActionLabel(LocationGateStatus.PROVIDER_DISABLED))
        assertEquals(
            "As apostas de Loterias só podem ser realizadas em território brasileiro.",
            locationMessage(LocationGateStatus.OUTSIDE_BRAZIL)
        )
        assertNull(locationActionLabel(LocationGateStatus.OUTSIDE_BRAZIL))
        assertEquals("Tentar novamente", locationActionLabel(LocationGateStatus.ERROR))
        assertNull(locationActionLabel(LocationGateStatus.CHECKING))
    }
}

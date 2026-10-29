package br.gov.caixa.loterias.feature.retail

import org.junit.Assert.assertFalse
import org.junit.Test

class PrizeReceiptDeliveryArchitectureTest {
    @Test fun `retained receipt owner does not depend on the Android Route helper`() {
        val owner = Class.forName(
            "br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel"
        )
        assertFalse(
            "A retained ViewModel must not own an Android Route delivery helper",
            owner.declaredFields.any { it.type.name.contains(".route.") }
        )
    }
}

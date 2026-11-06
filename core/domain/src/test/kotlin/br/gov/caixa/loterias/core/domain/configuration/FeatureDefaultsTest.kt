package br.gov.caixa.loterias.core.domain.configuration

import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureDefaultsTest {
    @Test fun existingCapabilitiesRemainAvailableWhenAnOldJsonOnlyConfiguresFaq() {
        val configuration = AppModulesConfiguration()
        AppModule.entries.filterNot { it == AppModule.SUPPORT_FAQ }.forEach { assertTrue(it.key, configuration.isEnabled(it)) }
        assertFalse(configuration.isEnabled(AppModule.SUPPORT_FAQ))
    }

    @Test fun explicitEmptyAvailabilityDoesNotAccidentallyApplyDefaults() {
        val configuration = AppModulesConfiguration(emptySet())
        assertTrue(AppModule.entries.none(configuration::isEnabled))
    }
}

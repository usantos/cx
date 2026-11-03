package br.gov.caixa.loterias.feature.onboarding.data.config

import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppModulesConfigurationParserTest {
    @Test fun enablesOnlyTheAllowlistedModule() {
        val configuration = requireNotNull(
            parseAppModulesConfiguration(
                """{"schema_version":1,"modules":{"support_faq":{"enabled":true},"future":{"enabled":true}}}"""
            )
        )
        assertEquals(AppModule.entries.toSet(), configuration.enabledModules)
        assertTrue(configuration.isEnabled(AppModule.SUPPORT_FAQ))
    }

    @Test fun acceptsExplicitDisabledAndMissingAllowlistedModule() {
        listOf(
            """{"schema_version":1,"modules":{"support_faq":{"enabled":false}}}""",
            """{"schema_version":1,"modules":{}}""",
            """{"schema_version":1,"modules":{"future":null}}"""
        ).forEach { encoded ->
            assertFalse(requireNotNull(parseAppModulesConfiguration(encoded)).isEnabled(AppModule.SUPPORT_FAQ))
        }
    }

    @Test fun missingEntriesUseTheirLocalDefaultsWithLegacyFaqOnlyJson() {
        val configuration = requireNotNull(
            parseAppModulesConfiguration("""{"schema_version":1,"modules":{"support_faq":{"enabled":false}}}""")
        )
        AppModule.entries.forEach { module ->
            assertEquals(module.key, module.defaultEnabled, configuration.isEnabled(module))
        }
    }

    @Test fun everyKnownModuleHonorsExplicitFalseAndTrueIndependently() {
        AppModule.entries.forEach { selected ->
            listOf(false, true).forEach { enabled ->
                val json = """{"schema_version":1,"modules":{"${selected.key}":{"enabled":$enabled}}}"""
                val configuration = requireNotNull(parseAppModulesConfiguration(json))
                AppModule.entries.forEach { module ->
                    assertEquals(
                        "${selected.key}=$enabled / ${module.key}",
                        if (module == selected) enabled else module.defaultEnabled,
                        configuration.isEnabled(module)
                    )
                }
            }
        }
    }

    @Test fun rejectsInvalidDocumentsInsteadOfPublishingDisabledConfiguration() {
        listOf(
            "", "null", "[]", "{}", "{",
            """{"schema_version":2,"modules":{}}""",
            """{"schema_version":"1","modules":{}}""",
            """{"schema_version":1.0,"modules":{}}""",
            """{"schema_version":true,"modules":{}}""",
            """{"schema_version":1}""",
            """{"schema_version":1,"modules":[]}""",
            """{"schema_version":1,"modules":{"support_faq":null}}""",
            """{"schema_version":1,"modules":{"support_faq":{}}}""",
            """{"schema_version":1,"modules":{"support_faq":{"enabled":"true"}}}""",
            """{"schema_version":1,"modules":{"support_faq":{"enabled":1}}}""",
            """{"schema_version":1,"modules":{"support_faq":{"enabled":null}}}""",
            """{"schema_version":1,"modules":{}} trailing"""
        ).forEach { encoded -> assertNull(encoded, parseAppModulesConfiguration(encoded)) }
    }
}

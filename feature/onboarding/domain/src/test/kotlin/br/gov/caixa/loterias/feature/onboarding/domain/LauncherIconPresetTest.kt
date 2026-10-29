package br.gov.caixa.loterias.feature.onboarding.domain

import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import org.junit.Assert.assertEquals
import org.junit.Test

class LauncherIconPresetTest {
    @Test fun allFiveRemoteValuesSelectTheirOwnPreset() {
        val values = listOf("default", "outubro_rosa", "setembro_amarelo", "novembro_azul", "agosto_lilas")
        assertEquals(values, LauncherIconPreset.entries.map { it.configValue })
        values.zip(LauncherIconPreset.entries).forEach { (value, expected) ->
            assertEquals(expected, LauncherIconPreset.fromConfig(value))
        }
    }

    @Test fun unknownOrMissingValuesReturnTheDefault() {
        listOf(null, "", " ", "https://example.invalid/icon.png", "arbitrary.Component", "copa").forEach {
            assertEquals(LauncherIconPreset.DEFAULT, LauncherIconPreset.fromConfig(it))
        }
    }

    @Test fun consoleNamesNormalizeCaseWhitespaceAndAccents() {
        assertEquals(LauncherIconPreset.AGOSTO_LILAS, LauncherIconPreset.fromConfig(" Agosto Lilás "))
        assertEquals(LauncherIconPreset.OUTUBRO_ROSA, LauncherIconPreset.fromConfig("OUTUBRO-ROSA"))
        assertEquals(LauncherIconPreset.DEFAULT, LauncherIconPreset.fromConfig("Padrão"))
    }
}

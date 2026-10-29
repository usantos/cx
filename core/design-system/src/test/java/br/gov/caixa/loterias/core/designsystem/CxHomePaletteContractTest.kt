// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CxHomePaletteContractTest {
    @Test
    fun homePaletteSharesCaixaBrandAndPreservesOpaqueAndOverlayRoles() {
        assertEquals(CaixaDSColorTokens.Blue700, CxHomeV2Colors.CFF005CA9)
        assertEquals(Color.White, CxHomeV2Colors.White)
        assertEquals(Color.Transparent, CxHomeV2Colors.Transparent)
        assertEquals(0x44003322, CxHomeV2Colors.C44003322.toArgb())
        assertEquals(0xFF003B71.toInt(), CxHomeV2Colors.CFF003B71.toArgb())
        assertNotEquals(CxHomeV2Colors.CFFF5B816, CxHomeV2Colors.CFFFFC52B)
        assertNotEquals(CxHomeV2Colors.CFF49279F, CxHomeV2Colors.CFF4A28A3)
    }
}

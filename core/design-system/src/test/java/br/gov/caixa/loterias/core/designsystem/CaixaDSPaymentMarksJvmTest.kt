package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentBrandMark
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentProvider
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPaymentProviderMark
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSPaymentMarksJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun recognizedCardNamesKeepProcessorDescriptionAndUnknownBrandsRemainReadable() {
        val brand = mutableStateOf("American Express")
        compose.setContent { CaixaDSTheme { CaixaDSPaymentBrandMark(brand.value, Modifier.size(60.dp, 40.dp)) } }
        listOf("American Express", "amex", "Elo", "Hiper", "hipercard", "Master Card", "master", "VISA").forEach { name ->
            compose.runOnIdle { brand.value = name }
            compose.onNodeWithContentDescription(name).assertIsDisplayed()
        }
        compose.runOnIdle { brand.value = "Bandeira nova" }
        compose.onNodeWithText("BANDEIRA NOVA").assertIsDisplayed()
        compose.onNodeWithContentDescription("Bandeira nova").assertDoesNotExist()
    }

    @Test
    fun providersOfferVectorInjectedAssetAndReadableFallback() {
        val resource = mutableStateOf(0)
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSPaymentProviderMark(CaixaDSPaymentProvider.MERCADO_PAGO, Modifier.size(100.dp, 40.dp))
                    CaixaDSPaymentProviderMark(CaixaDSPaymentProvider.RECARGA_PAY, Modifier.size(100.dp, 40.dp), resource.value)
                }
            }
        }
        compose.onNodeWithContentDescription("Mercado Pago").assertIsDisplayed()
        compose.onNodeWithText("recargapay").assertIsDisplayed()
        compose.runOnIdle { resource.value = R.drawable.legacy_marketplace_help }
        compose.onNodeWithContentDescription("Recarga Pay").assertIsDisplayed()
        compose.onNodeWithText("recargapay").assertDoesNotExist()
    }
}

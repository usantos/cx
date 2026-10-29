package br.gov.caixa.loterias.apostas

import android.content.ComponentName
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class VisitorLoginDialogGeometryUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private val message = "Para continuar utilizando a funcionalidade, você precisa efetuar o login no app Loterias Caixa. Deseja efetuar o login?"

    @Test fun defaultBettingDialogMatchesInstalledMaterialActivityActionWidthsAndRetains48dpHeight() {
        val exitMessage = "Ao voltar para a página inicial, sua aposta em edição será descartada. Deseja continuar?"
        compose.setContent {
            CaixaDSTheme { CaixaDSDialog("Atenção", exitMessage, "Confirmar", {}, "Cancelar") }
        }
        val nativePackage = "br.gov.caixa.loterias.apostas.cxv2validation"
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        val native = target.createPackageContext(nativePackage, Context.CONTEXT_IGNORE_SECURITY)
        val theme = native.resources.getIdentifier("Theme.SimulaMaterial", "style", nativePackage)
        val themed = ContextThemeWrapper(native, theme)
        val layout = LayoutInflater.from(themed).inflate(native.resources.getIdentifier("layout_dialog_padrao", "layout", nativePackage), null)
        val title = layout.findViewById<TextView>(native.resources.getIdentifier("titulo_dialog_padrao", "id", nativePackage)).apply { text = "Atenção"; measure(0, 0) }
        val bodyBounds = compose.onNodeWithText(exitMessage).fetchSemanticsNode().boundsInRoot
        val body = layout.findViewById<TextView>(native.resources.getIdentifier("conteudo_dialog_padrao", "id", nativePackage)).apply {
            text = exitMessage
            measure(View.MeasureSpec.makeMeasureSpec(bodyBounds.width.toInt(), View.MeasureSpec.EXACTLY), 0)
        }
        assertEquals("Native Material body natural line metrics", body.measuredHeight.toFloat(), bodyBounds.height, 1f)
        assertEquals("Native Material title natural line metrics", title.measuredHeight.toFloat(), compose.onNodeWithText("Atenção").fetchSemanticsNode().boundsInRoot.height, 1f)
        listOf("Cancelar" to "button_negativo", "Confirmar" to "button_positivo").forEach { (label, id) ->
            val widget = layout.findViewById<TextView>(native.resources.getIdentifier(id, "id", nativePackage)).apply { text = label; measure(0, 0) }
            val bounds = compose.onNodeWithText(label).fetchSemanticsNode().boundsInRoot
            assertEquals("Native Material $label width, paint=${widget.paint.measureText(label)}, padding=${widget.paddingLeft}/${widget.paddingRight}, min=${widget.minWidth}", widget.measuredWidth.toFloat(), bounds.width, 1f)
            assertEquals("Native Material $label 48dp action height", widget.measuredHeight.toFloat(), bounds.height, 1f)
        }
    }

    @Test fun defaultSingleEntendiActionRetainsNativeMaterialMinimumWidthAnd48dpHeight() {
        compose.setContent { CaixaDSTheme { CaixaDSDialog("Atenção", "Quantidade alterada", "Entendi", {}) } }
        val nativePackage = "br.gov.caixa.loterias.apostas.cxv2validation"
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        val native = target.createPackageContext(nativePackage, Context.CONTEXT_IGNORE_SECURITY)
        val themed = ContextThemeWrapper(native, native.resources.getIdentifier("Theme.SimulaMaterial", "style", nativePackage))
        val layout = LayoutInflater.from(themed).inflate(native.resources.getIdentifier("layout_dialog_padrao", "layout", nativePackage), null)
        val widget = layout.findViewById<TextView>(native.resources.getIdentifier("button_positivo", "id", nativePackage)).apply { text = "Entendi"; measure(0, 0) }
        val bounds = compose.onNodeWithText("Entendi").fetchSemanticsNode().boundsInRoot
        assertEquals("Native Material Entendi minimum width", widget.measuredWidth.toFloat(), bounds.width, 1f)
        assertEquals("Native Material Entendi 48dp height", widget.measuredHeight.toFloat(), bounds.height, 1f)
    }

    @Test fun visitorPromptMatchesInstalledNativePrivateActivityFontAndButtonMetrics() {
        compose.setContent {
            CaixaDSTheme {
                CaixaDSDialog("Atenção", message, "Sim", {}, "Não", visitorPrompt = true)
            }
        }
        val messageBounds = compose.onNodeWithText(message).fetchSemanticsNode().boundsInRoot
        val nativePackage = "br.gov.caixa.loterias.apostas.cxv2validation"
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        val native = target.createPackageContext(nativePackage, Context.CONTEXT_IGNORE_SECURITY)
        val info = target.packageManager.getActivityInfo(ComponentName(nativePackage,
            "br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity"), 0)
        val themed = ContextThemeWrapper(native, info.theme)
        val resources = native.resources
        val layout = LayoutInflater.from(themed).inflate(resources.getIdentifier("layout_dialog_padrao", "layout", nativePackage), null)
        fun text(id: String, value: String): TextView = layout.findViewById<TextView>(resources.getIdentifier(id, "id", nativePackage)).apply { text = value }
        val title = text("titulo_dialog_padrao", "Atenção")
        val body = text("conteudo_dialog_padrao", message)
        val no = text("button_negativo", "Não")
        val yes = text("button_positivo", "Sim")
        body.measure(View.MeasureSpec.makeMeasureSpec(messageBounds.width.toInt(), View.MeasureSpec.EXACTLY), 0)
        title.measure(0, 0); no.measure(0, 0); yes.measure(0, 0)
        assertEquals("Native regular TextView body line metrics", body.measuredHeight.toFloat(), messageBounds.height, 1f)
        assertEquals("Native bold title line metrics", title.measuredHeight.toFloat(), compose.onNodeWithText("Atenção").fetchSemanticsNode().boundsInRoot.height, 1f)
        listOf("Não" to no, "Sim" to yes).forEach { (label, widget) ->
            val bounds = compose.onNodeWithText(label).fetchSemanticsNode().boundsInRoot
            assertEquals("Native $label button height", widget.measuredHeight.toFloat(), bounds.height, 1f)
            assertEquals("Native $label button minimum width", widget.measuredWidth.toFloat(), bounds.width, 1f)
        }
    }
}

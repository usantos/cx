// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSModalBottomSheet
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSModalNavigationDrawer
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSProvideTextStyle
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSPullToRefreshBox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalMaterial3Api::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSContainersJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun scaffoldSuppliesBarInsetsAndTextStyleToItsContent() {
        var inheritedStyle = TextStyle.Default
        compose.setContent {
            CaixaDSTheme {
                CaixaDSScaffold(
                    topBar = { Box(Modifier.height(40.dp)) { CaixaDSText("Cabeçalho") } },
                    bottomBar = { Box(Modifier.height(32.dp)) { CaixaDSText("Rodapé") } },
                    floatingActionButton = { CaixaDSText("Ação flutuante") },
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { padding ->
                    CaixaDSProvideTextStyle(TextStyle(fontSize = 21.sp)) {
                        inheritedStyle = LocalTextStyle.current
                        CaixaDSText("Conteúdo", Modifier.padding(padding).testTag("body"))
                    }
                }
            }
        }
        listOf("Cabeçalho", "Rodapé", "Ação flutuante", "Conteúdo").forEach { compose.onNodeWithText(it).assertIsDisplayed() }
        val header = compose.onNodeWithText("Cabeçalho").fetchSemanticsNode().boundsInRoot
        val body = compose.onNodeWithTag("body").fetchSemanticsNode().boundsInRoot
        assertTrue(body.bottom > header.bottom)
        compose.runOnIdle { assertEquals(21.sp, inheritedStyle.fontSize) }
    }

    @Test
    fun alertDialogShowsSlotsAndKeepsConfirmationAndDismissalActionsDistinct() {
        val actions = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSAlertDialog(
                    onDismissRequest = { actions.add("outside") },
                    confirmButton = { CaixaDSTextButton({ actions.add("confirm") }) { CaixaDSText("Confirmar") } },
                    dismissButton = { CaixaDSTextButton({ actions.add("cancel") }) { CaixaDSText("Cancelar") } },
                    title = { CaixaDSText("Confirmação") },
                    text = { CaixaDSText("Deseja continuar?") },
                    icon = { CaixaDSText("!") }
                )
            }
        }
        listOf("Confirmação", "Deseja continuar?", "!").forEach { compose.onNodeWithText(it).assertIsDisplayed() }
        compose.onNodeWithText("Confirmar").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertEquals(listOf("confirm", "cancel"), actions) }
    }

    @Test
    fun drawerUsesCallerStateAndRetainsContentWhileClosed() {
        compose.setContent {
            CaixaDSTheme {
                val state = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                CaixaDSModalNavigationDrawer(drawerState = state, gesturesEnabled = false, drawerContent = {
                    ModalDrawerSheet {
                        CaixaDSText("Menu da conta")
                        CaixaDSTextButton({ scope.launch { state.close() } }) { CaixaDSText("Fechar") }
                    }
                }) {
                    CaixaDSTextButton({ scope.launch { state.open() } }) { CaixaDSText("Abrir") }
                }
            }
        }
        compose.onNodeWithText("Abrir").assertIsDisplayed().performClick()
        compose.onNodeWithText("Menu da conta").assertIsDisplayed()
        compose.onNodeWithText("Fechar").performClick()
        compose.onNodeWithText("Abrir").assertIsDisplayed()
    }

    @Test
    fun bottomSheetDisplaysContentAndClosesThroughConsumersAction() {
        val visible = mutableStateOf(true)
        var closed = 0
        compose.setContent {
            CaixaDSTheme {
                CaixaDSText("Página principal")
                if (visible.value) {
                    CaixaDSModalBottomSheet(
                        {
                            visible.value = false
                        },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        dragHandle = null
                    ) {
                        CaixaDSText("Resumo da aposta")
                        CaixaDSTextButton({
                            closed++
                            visible.value = false
                        }) { CaixaDSText("Concluir") }
                    }
                }
            }
        }
        compose.onNodeWithText("Resumo da aposta").assertIsDisplayed()
        compose.onNodeWithText("Concluir").performClick()
        compose.onNodeWithText("Resumo da aposta").assertDoesNotExist()
        compose.onNodeWithText("Página principal").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, closed) }
    }

    @Test
    fun progressAndRefreshExposeCallerStateWithoutStartingDuplicateRefreshes() {
        val progress = mutableStateOf(.25f)
        val refreshing = mutableStateOf(false)
        var refreshes = 0
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSCircularProgressIndicator({ progress.value }, Modifier.testTag("progress"))
                    CaixaDSPullToRefreshBox(refreshing.value, { refreshes++ }, Modifier.fillMaxSize(), indicator = {
                        if (refreshing.value) CaixaDSText("Atualizando")
                    }) { CaixaDSText("Resultados") }
                }
            }
        }
        assertEquals(.25f, compose.onNodeWithTag("progress").fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo].current)
        compose.onNodeWithText("Resultados").assertIsDisplayed()
        compose.runOnIdle {
            progress.value = .75f
            refreshing.value = true
        }
        assertEquals(.75f, compose.onNodeWithTag("progress").fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo].current)
        compose.onNodeWithText("Atualizando").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, refreshes) }
    }
}

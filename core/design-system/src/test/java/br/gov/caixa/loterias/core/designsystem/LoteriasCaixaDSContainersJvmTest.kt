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
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSModalBottomSheet
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSModalNavigationDrawer
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSProvideTextStyle
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSPullToRefreshBox
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
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
class LoteriasCaixaDSContainersJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun scaffoldSuppliesBarInsetsAndTextStyleToItsContent() {
        var inheritedStyle = TextStyle.Default
        compose.setContent {
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSScaffold(
                    topBar = { Box(Modifier.height(40.dp)) { LoteriasCaixaDSText("Cabeçalho") } },
                    bottomBar = { Box(Modifier.height(32.dp)) { LoteriasCaixaDSText("Rodapé") } },
                    floatingActionButton = { LoteriasCaixaDSText("Ação flutuante") },
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { padding ->
                    LoteriasCaixaDSProvideTextStyle(TextStyle(fontSize = 21.sp)) {
                        inheritedStyle = LocalTextStyle.current
                        LoteriasCaixaDSText("Conteúdo", Modifier.padding(padding).testTag("body"))
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
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSAlertDialog(
                    onDismissRequest = { actions.add("outside") },
                    confirmButton = { LoteriasCaixaDSTextButton({ actions.add("confirm") }) { LoteriasCaixaDSText("Confirmar") } },
                    dismissButton = { LoteriasCaixaDSTextButton({ actions.add("cancel") }) { LoteriasCaixaDSText("Cancelar") } },
                    title = { LoteriasCaixaDSText("Confirmação") },
                    text = { LoteriasCaixaDSText("Deseja continuar?") },
                    icon = { LoteriasCaixaDSText("!") }
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
            LoteriasCaixaDSTheme {
                val state = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                LoteriasCaixaDSModalNavigationDrawer(drawerState = state, gesturesEnabled = false, drawerContent = {
                    ModalDrawerSheet {
                        LoteriasCaixaDSText("Menu da conta")
                        LoteriasCaixaDSTextButton({ scope.launch { state.close() } }) { LoteriasCaixaDSText("Fechar") }
                    }
                }) {
                    LoteriasCaixaDSTextButton({ scope.launch { state.open() } }) { LoteriasCaixaDSText("Abrir") }
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
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSText("Página principal")
                if (visible.value) {
                    LoteriasCaixaDSModalBottomSheet(
                        {
                            visible.value = false
                        },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        dragHandle = null
                    ) {
                        LoteriasCaixaDSText("Resumo da aposta")
                        LoteriasCaixaDSTextButton({
                            closed++
                            visible.value = false
                        }) { LoteriasCaixaDSText("Concluir") }
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
            LoteriasCaixaDSTheme {
                Column {
                    LoteriasCaixaDSCircularProgressIndicator({ progress.value }, Modifier.testTag("progress"))
                    LoteriasCaixaDSPullToRefreshBox(refreshing.value, { refreshes++ }, Modifier.fillMaxSize(), indicator = {
                        if (refreshing.value) LoteriasCaixaDSText("Atualizando")
                    }) { LoteriasCaixaDSText("Resultados") }
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

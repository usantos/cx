package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSMercadoPagoBrand
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMercadoPagoPrizePanel
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSLocalTextStyle as LocalTextStyle
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPurchaseHelpIcon
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.components.LegacyRedemptionActionButton
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
internal fun PrizeMercadoPagoDataScreen(
    state: PrizeRedemptionUiState,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onConfirm: () -> Unit,
    onEmailChange: (String) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val largeFont = LocalDensity.current.fontScale > 1f
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_resgate_de_premio_via_mercado_pago_a5b5dc),
        onBack = onBack,
        topBarAction = {
            IconButton(onClick = onHelp, modifier = Modifier.uiId("retail.0081")) {
                CaixaDSPurchaseHelpIcon((Modifier.size(24.dp)).uiId("retail.0082"))
            }
        },

        containerColor = CaixaDSColorTokens.White,
        bottomBar = {
            Box(Modifier.fillMaxWidth().height(104.dp)) {
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(72.dp)
                        .background(CaixaDSColorTokens.MercadoPagoFooter)
                )
                LegacyRedemptionActionButton(
                    text = uiStrings.getString(LocalizedR.string.ui_resgatar_premio_e68bcd),
                    compactContent = true,
                    onClick = onConfirm,
                    modifier = (Modifier.align(Alignment.TopCenter)).uiId("retail.0083"),
                    color = CaixaDS.colors.teal
                )
            }
        },
        modifier = Modifier.uiId("retail.0084")
    ) { padding ->
        PrizeMercadoPagoBiasedBody((Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)).uiId("retail.0085")) {
            CaixaDSMercadoPagoBrand((Modifier.size(177.dp, 46.dp)).uiId("retail.0086"))
            CaixaDSMercadoPagoPrizePanel(
                amount = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(state.details?.netValue),
                modifier = (Modifier.fillMaxWidth().padding(top = 40.dp)).uiId("retail.0087")
            )
            Column(Modifier.fillMaxWidth().then(if (largeFont) Modifier.heightIn(min = 252.dp) else Modifier.height(252.dp))) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_para_receber_seu_premio_por_favor_co_95e3f1),
                    modifier = (Modifier.fillMaxWidth().padding(start = 16.dp, top = 30.dp)).uiId("retail.0088"),
                    color = CaixaDSColorTokens.BetAction,
                    fontSize = 18.199982.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    uiStrings.getString(LocalizedR.string.ui_e_mail_2550d9),
                    modifier = (Modifier.padding(start = 16.dp, top = 20.dp)).uiId("retail.0089"),
                    color = CaixaDSColorTokens.FavoriteBlue,
                    fontSize = 15.2.sp
                )
                PrizeMercadoPagoEmailField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    modifier = (Modifier.fillMaxWidth().padding(horizontal = 16.dp)).uiId("retail.0090")
                )
            }
        }
    }
}

/** ConstraintLayout compacto do MP: cabeçalho medido, margens de 20dp e viés vertical de 0.1. */
@Composable
private fun PrizeMercadoPagoBiasedBody(modifier: Modifier, content: @Composable () -> Unit) {
    BoxWithConstraints(modifier) {
        val viewportHeight = with(LocalDensity.current) { maxHeight.roundToPx() }
        Layout(
            content = { Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) { content() } },
            modifier = Modifier.fillMaxWidth().uiId("retail.0091").verticalScroll(rememberScrollState())
        ) { measurables, constraints ->
            val child = measurables.single().measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
            val margin = 20.dp.roundToPx()
            val height = maxOf(viewportHeight, child.height + margin * 2)
            val top = margin + ((height - child.height - margin * 2) * .1f).roundToInt()
            layout(constraints.maxWidth, height) { child.placeRelative(0, top) }
        }
    }
}

@Composable
private fun PrizeMercadoPagoEmailField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val lineColor = CaixaDS.colors.line
    val label = androidx.compose.ui.res.stringResource(LocalizedR.string.ui_e_mail_2550d9)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = (
            modifier
                .semantics { contentDescription = label }
                .height(54.dp)
                .drawBehind {
                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height - 1.dp.toPx()),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height - 1.dp.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            ).uiId("retail.0092"),
        textStyle = CaixaDS.typography.bodyLarge.copy(
            color = CaixaDSColorTokens.BetAction,
            fontSize = 20.sp,
            lineHeight = 24.sp
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                innerTextField()
            }
        }
    )
}

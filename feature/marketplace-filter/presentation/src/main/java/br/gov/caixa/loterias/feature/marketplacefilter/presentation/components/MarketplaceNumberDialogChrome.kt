package br.gov.caixa.loterias.feature.marketplacefilter.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as LoteriasCaixaDSResources
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterButton as ActionButton
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterStyle
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplacefilter.R as LocalizedR

@Composable
internal fun NumberDialogHeader(title: String, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        modifier = Modifier.fillMaxWidth().height(39.dp)
            .background(LoteriasCaixaDSMarketplaceFilterStyle.accent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            modifier = (Modifier.padding(start = 24.dp).weight(1f)).uiId("filter.0024"),
            color = LoteriasCaixaDS.colors.onBrand,
            style = LoteriasCaixaDSMarketplaceFilterStyle.dialogHeading
        )
        Row(
            modifier = Modifier.fillMaxHeight().uiId("filter.0025").clickable(onClick = onDismiss).padding(end = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(LoteriasCaixaDSResources.drawable.legacy_marketplace_filter_close),
                contentDescription = null,
                tint = LoteriasCaixaDS.colors.onBrand,
                modifier = (Modifier.size(10.dp)).uiId("filter.0026")
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_fechar_0f2bd8),
                modifier = (Modifier.padding(start = 7.dp)).uiId("filter.0027"),
                color = LoteriasCaixaDS.colors.onBrand,
                style = LoteriasCaixaDSMarketplaceFilterStyle.dialogHeading
            )
        }
    }
}

@Composable
internal fun LegacyDialogButton(text: String, outlined: Boolean, onClick: () -> Unit, modifier: Modifier) {
    ActionButton(
        text = text,
        outlined = outlined,
        onClick = onClick,
        modifier = (modifier).uiId("filter.0028"),
        height = 31.dp,
        textSize = NUMBER_DIALOG_ACTION_TEXT_SIZE,
        lineHeight = 16.2.sp,
        contentColor = if (outlined) LoteriasCaixaDSMarketplaceFilterStyle.heading else LoteriasCaixaDSColorTokens.White
    )
}

@Composable
internal fun NumberDialogActions(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 16.dp)) {
        LegacyDialogButton(
            stringResource(LocalizedR.string.format_cancelar),
            true,
            onDismiss,
            (Modifier.weight(1f).padding(end = 5.dp)).uiId("filter.0029")
        )
        LegacyDialogButton(
            stringResource(LocalizedR.string.format_confirmar),
            false,
            onConfirm,
            (Modifier.weight(1f).padding(start = 5.dp)).uiId("filter.0030")
        )
    }
}

private const val NUMBER_DIALOG_ACTION_TEXT_SIZE = 12

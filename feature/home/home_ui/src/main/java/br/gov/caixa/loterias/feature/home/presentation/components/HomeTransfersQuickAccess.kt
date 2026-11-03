package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import java.math.BigDecimal

@Composable
internal fun HomeTransfersQuickAccess(
    socialTransfers: SocialTransfers?,
    loadingSocialTransfers: Boolean,
    transfersAmount: Double,
    modifier: Modifier,
    onSocialTransfers: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    HomeQuickAccessItem(
        modifier = modifier.uiId("home.0051"),
        title = when {
            socialTransfers != null -> formatBrazilianCurrency(BigDecimal.valueOf(transfersAmount))
            loadingSocialTransfers -> uiStrings.getString(DeliveryR.string.ui_carregando_125857)
            else -> uiStrings.getString(DeliveryR.string.ui_dados_indisponiveis_decf8d)
        },
        description = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(uiStrings.getString(DeliveryR.string.final_quick_transferred))
            }
            append(uiStrings.getString(DeliveryR.string.ui_para_boas_causas_570deb))
            socialTransfers?.baseDate?.takeLast(TRANSFER_YEAR_LENGTH)?.takeIf(String::isNotBlank)?.let { year ->
                append(uiStrings.getString(DeliveryR.string.final_quick_transfer_year, year))
            }
        },
        descriptionSize = 14.sp,
        color = LoteriasCaixaDSColorTokens.TealAction,
        icon = {
            Image(
                painter = painterResource(DeliveryR.drawable.icon_repassados),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        },
        onClick = onSocialTransfers
    )
}

private const val TRANSFER_YEAR_LENGTH = 4

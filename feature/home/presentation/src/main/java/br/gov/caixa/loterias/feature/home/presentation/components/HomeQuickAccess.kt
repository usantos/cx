package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.initialHomeTransfers
import br.gov.caixa.loterias.feature.home.presentation.mapper.projectedHomeTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.math.BigDecimal
import kotlin.math.PI
import kotlin.math.cos

@Composable
internal fun HomeQuickAccessPanel(
    socialTransfers: SocialTransfers?,
    loadingSocialTransfers: Boolean,
    onReadTicket: () -> Unit,
    onResponsibleGaming: () -> Unit,
    onSocialTransfers: () -> Unit,
    onWomenSupport: () -> Unit,
    onContentHeightChanged: (Dp) -> Unit = {},
    itemModifier: @Composable (Int) -> Modifier = { Modifier },
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val density = LocalDensity.current
    val transfersAmount = rememberHomeTransfersAmount(socialTransfers)
    Column(
        modifier = Modifier.fillMaxWidth().onSizeChanged {
            onContentHeightChanged(with(density) { it.height.toDp() })
        }.padding(top = 10.dp),
        horizontalAlignment = Alignment.End
    ) {
        HomeQuickAccessItem(
            modifier = (itemModifier(0)).uiId("home.0049"),
            title = uiStrings.getString(LocalizedR.string.ui_conferir_bilhetes_1465ae),
            description = AnnotatedString(uiStrings.getString(LocalizedR.string.final_quick_ticket_description)),
            color = CaixaDSColorTokens.QuickTicket,
            icon = {
                Image(
                    painter = painterResource(R.drawable.icon_bilhetes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            },
            onClick = onReadTicket
        )
        HomeQuickAccessItem(
            modifier = (itemModifier(1)).uiId("home.0050"),
            title = uiStrings.getString(LocalizedR.string.ui_jogo_responsavel_01eb13),
            description = AnnotatedString(uiStrings.getString(LocalizedR.string.final_quick_responsible_description)),
            color = CaixaDSColorTokens.Blue700,
            icon = { AgeRestrictionIcon() },
            onClick = onResponsibleGaming
        )
        HomeQuickAccessItem(
            modifier = (itemModifier(2)).uiId("home.0051"),
            title = when {
                socialTransfers != null -> formatBrazilianCurrency(BigDecimal.valueOf(transfersAmount))
                loadingSocialTransfers -> uiStrings.getString(LocalizedR.string.ui_carregando_125857)
                else -> uiStrings.getString(LocalizedR.string.ui_dados_indisponiveis_decf8d)
            },
            description = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(uiStrings.getString(LocalizedR.string.final_quick_transferred))
                }
                append(uiStrings.getString(LocalizedR.string.ui_para_boas_causas_570deb))
                socialTransfers?.baseDate?.takeLast(4)?.takeIf(String::isNotBlank)?.let { year ->
                    append(uiStrings.getString(LocalizedR.string.final_quick_transfer_year, year))
                }
            },
            descriptionSize = 14.sp,
            color = CaixaDSColorTokens.TealAction,
            icon = {
                Image(
                    painter = painterResource(R.drawable.icon_repassados),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            },
            onClick = onSocialTransfers
        )
        HomeQuickAccessItem(
            modifier = (itemModifier(3)).uiId("home.0052"),
            title = uiStrings.getString(LocalizedR.string.ui_central_de_atendimento_a_mulher_24d146),
            description = AnnotatedString(uiStrings.getString(LocalizedR.string.final_quick_women_description)),
            color = CaixaDSColorTokens.WomenSupport,
            icon = { WomenSupportIcon() },
            onClick = onWomenSupport
        )
    }
}

@Composable
private fun HomeQuickAccessItem(
    title: String,
    modifier: Modifier = Modifier,
    description: AnnotatedString,
    color: Color,
    descriptionSize: TextUnit = 13.sp,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.width(294.dp).heightIn(min = 60.dp).uiId("home.0053").clickable(onClick = onClick),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.width(199.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = title,
                color = color,
                style = homeLegacyTextStyle(14.sp, FontWeight.Bold),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("home.0054")
            )
            Text(
                text = description,
                color = CaixaDSColorTokens.BetAction,
                style = homeLegacyTextStyle(descriptionSize),
                textAlign = TextAlign.End,
                modifier = Modifier.uiId("home.0055")
            )
        }
        Box(
            modifier = Modifier.width(95.dp).height(60.dp).padding(start = 20.dp),
            contentAlignment = Alignment.TopStart
        ) {
            icon()
        }
    }
}

@Composable
private fun AgeRestrictionIcon() {
    Image(painterResource(br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_ic_jr_menu_trevo), null, Modifier.size(40.dp))
}

@Composable
private fun WomenSupportIcon() {
    Image(painterResource(br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_ic_disque_180), null, Modifier.size(40.dp))
}

@Composable
private fun rememberHomeTransfersAmount(transfers: SocialTransfers?): Double {
    var amount by remember(transfers) { mutableDoubleStateOf(transfers?.initialHomeTransfers() ?: 0.0) }
    val fraction = remember(transfers) { Animatable(0f) }
    LaunchedEffect(transfers) {
        if (transfers == null) return@LaunchedEffect
        while (isActive) {
            val start = amount
            val target = transfers.projectedHomeTransfers(System.currentTimeMillis())
            fraction.snapTo(0f)
            delay(400)
            fraction.animateTo(
                1f,
                tween(
                    1_000,
                    easing = Easing { progress ->
                        ((cos((progress + 1.0) * PI) / 2.0) + 0.5).toFloat()
                    }
                )
            ) {
                amount = start + (target - start) * value
            }
        }
    }
    return amount
}

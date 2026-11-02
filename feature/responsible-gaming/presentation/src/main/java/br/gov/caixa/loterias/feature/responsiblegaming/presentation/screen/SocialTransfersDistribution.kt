package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.SocialBlock
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.SocialCategoryData
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.components.SocialCategoryPair
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.socialAmount

@Composable
internal fun ColumnScope.SocialTransfersAllocationIntro() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        // Iguala a coluna mais alta como em cardsContainer legado com wrap_content.
        Modifier.fillMaxWidth().heightIn(min = 130.dp).height(IntrinsicSize.Min).background(
            Brush.horizontalGradient(listOf(LoteriasCaixaDS.colors.headerStart, LoteriasCaixaDS.colors.headerEnd))
        )
    ) {
        SocialBlock(
            first = uiStrings.getString(LocalizedR.string.ui_para_cada_e80c97),
            value = "R$1,00",
            description = uiStrings.getString(LocalizedR.string.ui_apostados_nas_loterias_caixa_2ebefb),
            background = LoteriasCaixaDSColorTokens.SocialPanelBlue,
            foreground = LoteriasCaixaDSColorTokens.White,
            valueColor = LoteriasCaixaDSColorTokens.SocialPanelLime,
            backgroundResource = R.drawable.repassequadro1,
            modifier = (Modifier.weight(1f)).uiId("rg.0054")
        )
        SocialBlock(
            first = "",
            value = "R$0,48*",
            description = uiStrings.getString(LocalizedR.string.ui_sao_destinados_a_programas_sociais_25cae5),
            background = LoteriasCaixaDSColorTokens.SocialPanelLime,
            foreground = LoteriasCaixaDS.colors.text,
            valueColor = LoteriasCaixaDSColorTokens.SocialPanelBlue,
            backgroundResource = R.drawable.repassequadro2,
            modifier = (Modifier.weight(1f)).uiId("rg.0055")
        )
    }
    Text(
        uiStrings.getString(LocalizedR.string.ui_o_valor_inclui_a_parcela_corresponde_707993),
        (Modifier.fillMaxWidth().background(LoteriasCaixaDS.colors.filterLine).padding(16.dp)).uiId("rg.0056"),
        color = LoteriasCaixaDSColorTokens.SocialGraphite,
        fontSize = 18.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun ColumnScope.SocialTransfersDistribution(transfers: SocialTransfers?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Image(
        painterResource(R.drawable.ilustra_repasses),
        null,
        Modifier.align(Alignment.CenterHorizontally).padding(top = 40.dp).size(50.dp),
        contentScale = ContentScale.Fit
    )
    Box(
        Modifier.align(Alignment.CenterHorizontally).padding(top = 15.dp).width(230.dp).height(2.dp).background(LoteriasCaixaDS.colors.line)
    )
    Text(
        uiStrings.getString(LocalizedR.string.ui_entenda_como_2905aa),
        (Modifier.fillMaxWidth().padding(top = 17.dp)).uiId("rg.0057"),
        color = LoteriasCaixaDS.colors.brand,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Text(
        uiStrings.getString(LocalizedR.string.ui_esse_dinheiro_e_distribuido_cbd1af),
        (Modifier.fillMaxWidth()).uiId("rg.0058"),
        color = LoteriasCaixaDS.colors.brand,
        fontSize = 18.sp,
        textAlign = TextAlign.Center
    )
    SocialCategoryPair(
        left = SocialCategoryData(
            uiStrings.getString(LocalizedR.string.ui_educacao_620915),
            transfers?.let { socialAmount(it.amountFor(it.educationPercentage)) },
            LoteriasCaixaDSColorTokens.SocialEducation
        ),
        right = SocialCategoryData(
            "Cultura",
            transfers?.let {
                socialAmount(it.amountFor(it.culturePercentage))
            },
            LoteriasCaixaDSColorTokens.SocialCulture
        ),
        modifier = (Modifier.padding(top = 23.dp)).uiId("rg.0059")
    )
    SocialCategoryPair(
        left = SocialCategoryData(
            "Esporte",
            transfers?.let {
                socialAmount(it.amountFor(it.sportPercentage))
            },
            LoteriasCaixaDSColorTokens.SocialSport
        ),
        right = SocialCategoryData(
            uiStrings.getString(LocalizedR.string.ui_seguranca_d5668a),
            transfers?.let { socialAmount(it.amountFor(it.securityPercentage)) },
            LoteriasCaixaDSColorTokens.SocialSecurity
        ),
        modifier = Modifier.uiId("rg.0060")
    )
    SocialCategoryPair(
        left = SocialCategoryData(
            uiStrings.getString(LocalizedR.string.ui_seguridade_social_6f2d6c),
            transfers?.let { socialAmount(it.amountFor(it.socialSecurityPercentage)) },
            LoteriasCaixaDSColorTokens.SocialSecuritySystem
        ),
        right = SocialCategoryData(
            "Outros",
            transfers?.let {
                socialAmount(it.amountFor(it.otherPercentage))
            },
            LoteriasCaixaDSColorTokens.SocialOther
        ),
        modifier = Modifier.uiId("rg.0061")
    )
}

@Composable
internal fun SocialTransfersFooter(onBack: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.fillMaxWidth().height(180.dp).background(LoteriasCaixaDSColorTokens.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(Modifier.fillMaxWidth().height(100.dp).background(LoteriasCaixaDSColorTokens.SocialCanvas))
        Button(
            onClick = onBack,
            modifier = (Modifier.padding(bottom = 43.dp).width(206.dp).heightIn(min = 52.dp)).uiId("rg.0062"),
            shape = RoundedCornerShape(4.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LoteriasCaixaDSColorTokens.SocialGraphite,
                contentColor = LoteriasCaixaDSColorTokens.White
            )
        ) {
            Text(
                androidx.compose.ui.res.stringResource(
                    br.gov.caixa.loterias.feature.responsiblegaming.R.string.social_transfers_bet_action
                ),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.uiId("rg.0063")
            )
        }
    }
}

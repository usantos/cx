package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSModalBottomSheet as ModalBottomSheet
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.state.showCaixaDSMessage
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSResponsibleGamingColors
import br.gov.caixa.loterias.feature.responsiblegaming.R as LocalizedR

/** Contatos, textos, ordem e destinos de BottomSheetFragment/fragment_bottom_sheet. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AssessmentHelpSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = CaixaDSColorTokens.White,
        dragHandle = {
            Box(
                Modifier.padding(top = 20.dp).size(44.dp, 5.dp)
                    .background(CaixaDSResponsibleGamingColors.Handle, RoundedCornerShape(3.dp))
            )
        },
        modifier = Modifier.uiId("rg.0035")
    ) {
        Text(
            AssessmentHelpText.Title,
            (Modifier.align(Alignment.CenterHorizontally).padding(top = 20.dp)).uiId("rg.0036"),
            color = CaixaDSColorTokens.BetNavigationLink,
            fontFamily = CaixaDSRegularFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            AssessmentHelpText.Description,
            (Modifier.padding(20.dp)).uiId("rg.0037"),
            color = CaixaDSResponsibleGamingColors.Description,
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
        Column(
            Modifier.fillMaxWidth().heightIn(max = 500.dp).uiId("rg.0038").verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 100.dp)
        ) {
            ContactText(AssessmentHelpText.CaixaTitle, bold = true)
            ContactText(AssessmentHelpText.CaixaPhoneLabel, bold = true, cleanEmphasis = true)
            ContactText(AssessmentHelpText.CaixaMetropolitanPhone, target = "tel:40040104")
            ContactText(AssessmentHelpText.CaixaOtherRegionsPhone, target = "tel:08001040104")
            ContactText(AssessmentHelpText.LotteryOption)
            ContactText(AssessmentHelpText.ResponsibleGamingOption)
            ContactText(AssessmentHelpText.CapsTitle, bold = true, section = true)
            ContactText(AssessmentHelpText.CapsLink, target = AssessmentHelpText.CAPS_URL, web = true)
            ContactText(AssessmentHelpText.ProamjoTitle, section = true)
            ContactText(AssessmentHelpText.ProamjoAddress, address = true)
            ContactText(AssessmentHelpText.ProamjoPhones)
            ContactText(AssessmentHelpText.ProamjoEmail, target = "mailto:proamjo.secretaria@gmail.com", web = true)
            ContactText(AssessmentHelpText.ViverBemTitle, bold = true, section = true)
            ContactText(AssessmentHelpText.ViverBemAddress, address = true)
            ContactText(AssessmentHelpText.ViverBemPhone, target = "tel:1123077804")
            ContactText(AssessmentHelpText.ViverBemEmail, target = "mailto:contato@associacaoviverbem.org.br", web = true)
            ContactText(AssessmentHelpText.ProadTitle, section = true)
            ContactText(AssessmentHelpText.ProadAddress, address = true)
            ContactText(AssessmentHelpText.ProadPhones)
            ContactText(AssessmentHelpText.ProadEmail, target = "mailto:mpm_fto@uol.com.br", web = true)
            ContactText(AssessmentHelpText.SantaCasaTitle, section = true)
            ContactText(AssessmentHelpText.SantaCasaAddress, address = true)
            ContactText(AssessmentHelpText.SantaCasaPhone, target = "tel:2125330118")
            ContactText(AssessmentHelpText.AnonymousGamblersTitle, bold = true, section = true)
            ContactText(AssessmentHelpText.AnonymousGamblersPhones)
            ContactText(AssessmentHelpText.AnonymousGamblersLink, target = AssessmentHelpText.ANONYMOUS_GAMBLERS_URL, web = true)
        }
    }
}

@Composable
private fun ContactText(
    value: String,
    bold: Boolean = false,
    section: Boolean = false,
    address: Boolean = false,
    cleanEmphasis: Boolean = false,
    target: String? = null,
    web: Boolean = false,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val context = LocalContext.current
    val text = value.let { if (cleanEmphasis) it.replace("_", "") else it }
    val linkedText = buildAnnotatedString {
        append(text)
        // android:autoLink="phone" nativo expõe cada número separadamente.
        if (target == null) {
            Regex("\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4,5}").findAll(text).forEach { phone ->
                addLink(LinkAnnotation.Url("tel:${phone.value.filter(Char::isDigit)}"), phone.range.first, phone.range.last + 1)
            }
        }
    }
    Text(
        linkedText,
        (
            Modifier.padding(top = if (section) 20.dp else 0.dp).padding(2.dp).then(
                if (target == null) {
                    Modifier
                } else {
                    Modifier.uiId("rg.0039").clickable {
                        openContactTarget(context, target, uiStrings)
                    }
                }
            )
            ).uiId("rg.0040"),
        color = when {
            web -> CaixaDSResponsibleGamingColors.Link
            address -> CaixaDSResponsibleGamingColors.Address
            else -> CaixaDSColorTokens.BetAction
        },
        fontFamily = CaixaDSRegularFontFamily,
        fontSize = 14.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        textDecoration = if (target != null) TextDecoration.Underline else null
    )
}

private fun openContactTarget(context: android.content.Context, target: String, uiStrings: android.content.res.Resources) {
    if (target.startsWith("mailto:")) {
        val email = Intent(Intent.ACTION_SENDTO, "mailto:".toUri())
            .putExtra(Intent.EXTRA_EMAIL, arrayOf(target.removePrefix("mailto:")))
        try {
            context.startActivity(
                Intent.createChooser(
                    email,
                    uiStrings.getString(LocalizedR.string.ui_enviar_e_mail_usando_82df30)
                )
            )
        } catch (_: android.content.ActivityNotFoundException) {
            showCaixaDSMessage(
                context,
                uiStrings.getString(LocalizedR.string.ui_nao_ha_nenhum_aplicativo_de_e_mail_i_e2f36a)
            )
        }
    } else {
        val intent = Intent(Intent.ACTION_VIEW, target.toUri())
        if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
    }
}

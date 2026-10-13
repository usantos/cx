package br.gov.caixa.loterias.feature.support

import br.gov.caixa.loterias.core.designsystem.caixaDSLegacyDrawablePainter
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSAuthenticationLoadingDialog
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSR
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDivider
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPrimaryButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.domain.support.SupportQuestion

@Composable
fun SupportScreen(
    state: SupportUiState,
    onAction: (SupportAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) CaixaDSAuthenticationLoadingDialog()
    if (state.mode == SupportMode.TERMS_READ_ONLY) {
        ReadOnlyTermsScreen(state, { onAction(SupportAction.Back) }, modifier)
        return
    }
    if (state.mode == SupportMode.GAME_HELP) {
        GameHelpScreen(
            modalityId = state.gameHelpModalityId,
            modalityName = state.gameHelpModalityName,
            onBack = { onAction(SupportAction.Back) },
            modifier = modifier,
        )
        return
    }
    CaixaDSLegacyScaffold(
        title = when {
            state.selectedSectionId != null -> state.selectedSectionTitle.legacyCapitalized()
            state.acceptanceRequired || state.showTermsTabs -> "Loterias Caixa"
            else -> "Dúvidas"
        },
        onBack = { onAction(SupportAction.Back) },
        modifier = modifier.fillMaxSize().testTag("support_screen"),
        containerColor = CaixaDSColorTokens.White,
        customTopBar = if (state.selectedSectionId != null || (!state.showTermsTabs && !state.acceptanceRequired)) {
            { SupportCustomTopBar(if (state.selectedSectionId != null) state.selectedSectionTitle.legacyCapitalized() else "Dúvidas",
                { onAction(SupportAction.Back) }) }
        } else null,
        bottomBar = {
            if (state.acceptanceRequired && state.selectedTab == SupportTab.TERMS) {
                Box(Modifier.fillMaxWidth().height(131.dp).background(CaixaDSColorTokens.White)) {
                    Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSColorTokens.LegacyDrawerDivider))
                    Row(
                        modifier = Modifier.padding(start = 17.dp, top = 10.dp).toggleable(
                            value = state.termsAccepted,
                            role = Role.Checkbox,
                            onValueChange = { onAction(SupportAction.ToggleTermsAcceptance) },
                        ),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Image(
                            painterResource(if (state.termsAccepted) CaixaDSR.drawable.legacy_check2 else CaixaDSR.drawable.legacy_check1),
                            contentDescription = "Aceitar Termos de Uso",
                            modifier = Modifier.padding(top = 6.dp).size(20.dp),
                        )
                        Text("Declaro ter lido e concordado com os\ntermos de uso", Modifier.padding(start = 20.dp),
                            color = CaixaDS.colors.legacyGraphite,
                            style = TextStyle(fontFamily = CaixaDSLegacyRegularFontFamily, fontSize = 15.sp,
                                platformStyle = PlatformTextStyle(includeFontPadding = true)))
                    }
                    Button(
                        onClick = { onAction(SupportAction.AcceptTerms) },
                        enabled = state.termsAccepted,
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 55.dp).height(56.dp),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CaixaDSColorTokens.YellowAction, contentColor = CaixaDSColorTokens.White),
                        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp),
                    ) { Text("Continuar", style = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, platformStyle = PlatformTextStyle(includeFontPadding = true))) }
                }
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (state.showTermsTabs && state.selectedSectionId == null) {
                SupportTabs(state.selectedTab, onAction)
            }
            when {
                state.errorMessage != null -> Text(
                    text = state.errorMessage,
                    color = CaixaDS.colors.error,
                    modifier = Modifier.padding(16.dp),
                )
                state.selectedTab == SupportTab.FAQ -> FaqContent(state, onAction)
                else -> HtmlContent(state.htmlContent)
            }
        }
    }
}

@Composable
private fun SupportTabs(selected: SupportTab, onAction: (SupportAction) -> Unit) {
    val style = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 12.sp, platformStyle = PlatformTextStyle(includeFontPadding = true))
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val termsWidth = with(density) { measurer.measure("Termo de uso", style).size.width.toDp() }
    val faqWidth = with(density) { measurer.measure("Dúvidas", style).size.width.toDp() }
    // XML RadioButtons use wrap_content + equal weight: distribute remaining space equally.
    BoxWithConstraints(Modifier.fillMaxWidth().height(56.dp)) {
        val availableWidth = maxWidth
        val firstWidth = (availableWidth + termsWidth - faqWidth) / 2
        Row(Modifier.fillMaxSize()) {
            listOf(SupportTab.TERMS to "Termo de uso", SupportTab.FAQ to "Dúvidas").forEach { (tab, label) ->
                val isSelected = selected == tab
                Box(Modifier.width(if (tab == SupportTab.TERMS) firstWidth else availableWidth - firstWidth)
                    .fillMaxSize()
                    .background(if (isSelected) CaixaDS.colors.legacyTeal else CaixaDSColorTokens.LegacyTicketDisabledBackground)
                    .clickable { onAction(SupportAction.SelectTab(tab)) }, contentAlignment = Alignment.Center) {
                    if (isSelected) Image(painterResource(CaixaDSR.drawable.legacy_tab_seta), null,
                        Modifier.align(Alignment.BottomCenter))
                    Text(label, color = if (isSelected) CaixaDSColorTokens.White else CaixaDS.colors.legacyTeal, style = style)
                }
            }
        }
    }
}

@Composable
private fun FaqContent(state: SupportUiState, onAction: (SupportAction) -> Unit) {
    if (state.selectedSectionId == null) {
        FaqSections(state, onAction)
    } else {
        FaqQuestions(state, onAction)
    }
}

@Composable
private fun FaqSections(state: SupportUiState, onAction: (SupportAction) -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().padding(top = 22.dp),
    ) {
        items(state.sections, key = { it.id }) { section ->
            val divider = CaixaDS.colors.legacyLine
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(51.dp)
                    .clickable { onAction(SupportAction.OpenSection(section.id, section.title)) }
                    .padding(horizontal = 16.dp)
                    .drawBehind {
                        if (section != state.sections.firstOrNull()) drawLine(divider, Offset.Zero,
                            Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = section.title,
                    color = CaixaDS.colors.legacyGraphite,
                    style = supportRegularStyle(14),
                    modifier = Modifier.weight(.8f),
                )
                Box(Modifier.weight(.2f), contentAlignment = Alignment.CenterEnd) {
                    Image(painterResource(R.drawable.legacy_support_shape_23_copy_3),
                        "Abrir ${section.title}", Modifier.size(width = 10.dp, height = 16.dp).rotate(90f))
                }
            }
        }
    }
}

@Composable
private fun FaqQuestions(state: SupportUiState, onAction: (SupportAction) -> Unit) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(state.questions, key = SupportQuestion::id) { question ->
            val expanded = question.id in state.expandedQuestionIds
            val divider = CaixaDS.colors.legacyLine
            Column(
                Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(horizontal = 16.dp)
                    .drawBehind {
                        if (question != state.questions.firstOrNull()) drawLine(divider, Offset.Zero,
                            Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
                    },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onAction(SupportAction.ToggleQuestion(question.id)) }.padding(vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = question.title,
                        color = if (expanded) CaixaDS.colors.legacyTeal else CaixaDS.colors.legacyGraphite,
                        style = supportRegularStyle(if (expanded) 16 else 14),
                        modifier = Modifier.weight(.8f),
                    )
                    Box(Modifier.weight(.2f), contentAlignment = Alignment.CenterEnd) {
                        Image(painterResource(if (expanded) R.drawable.legacy_support_shape_23_copy_3 else R.drawable.legacy_support_shape_23_copy_2),
                            if (expanded) "Recolher resposta" else "Expandir resposta",
                            Modifier.size(width = 10.dp, height = 16.dp))
                    }
                }
                if (expanded) {
                    FaqAnswer(question.answer, Modifier.padding(start = 19.dp, end = 3.dp, bottom = 18.dp))
                }
            }
        }
    }
}

@Composable
private fun FaqAnswer(answer: String, modifier: Modifier) {
    val context = LocalContext.current
    val linkColor = CaixaDS.colors.legacyTeal
    var pendingUrl by remember(answer) { mutableStateOf<String?>(null) }
    val text = remember(answer, linkColor) {
        val builder = AnnotatedString.Builder(answer)
        val matcher = Patterns.WEB_URL.matcher(answer)
        while (matcher.find()) {
            val candidate = matcher.group()
            val parsed = Uri.parse(candidate)
            val scheme = parsed.scheme
            val host = if (scheme == null) Uri.parse("https://$candidate").host else parsed.host
            val url = supportAnswerUrl(candidate, matcher.start(), answer, scheme, host) ?: continue
            builder.addStyle(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
                matcher.start(), matcher.end())
            builder.addStringAnnotation("url", url, matcher.start(), matcher.end())
        }
        builder.toAnnotatedString()
    }
    ClickableText(text = text, modifier = modifier,
        style = supportRegularStyle(14).copy(color = CaixaDS.colors.legacyGraphite),
        onClick = { offset -> pendingUrl = text.getStringAnnotations("url", offset, offset).firstOrNull()?.item })
    pendingUrl?.let { url ->
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Você será direcionado para a WEB. Deseja continuar?",
            positiveLabel = "Confirmar",
            onPositive = {
                pendingUrl = null
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
            },
            negativeLabel = "Cancelar",
            onNegative = { pendingUrl = null },
        )
    }
}

private fun String.legacyCapitalized(): String = lowercase().replaceFirstChar { it.titlecase() }

private fun supportRegularStyle(size: Int) = TextStyle(fontFamily = CaixaDSLegacyRegularFontFamily,
    fontSize = size.sp, platformStyle = PlatformTextStyle(includeFontPadding = true))

@Composable
private fun HtmlContent(html: String) {
    // TermosUsoActivity loads the original server document in WebView; preserve its CSS and links.
    CaixaDSComposeTermsWebView(
        html = html,
        modifier = Modifier.fillMaxSize().padding(start = 15.dp, end = 15.dp, top = 40.dp, bottom = 30.dp)
            .testTag("support_html"),
    )
}

@Composable
private fun GameHelpScreen(
    modalityId: Int,
    modalityName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lotteryStyle = caixaDSLotteryStyle(modalityId, modalityName)
    val headerDark = when (modalityId) {
        8 -> CaixaDSColorTokens.LegacyLotomaniaDark // AjudaActivity usa esta cor para Lotofácil.
        14 -> CaixaDSColorTokens.LotogolDark
        else -> lotteryStyle.dark
    }
    val headerLight = when (modalityId) {
        16 -> CaixaDSColorTokens.LegacyHelpLotomania
        20 -> CaixaDSColorTokens.LegacyHelpTimemania
        19 -> CaixaDSColorTokens.LegacyHelpLoteca
        14 -> CaixaDSColorTokens.LegacyHelpLotogol
        else -> lotteryStyle.light
    }
    val title = modalityName.ifBlank { "Mega-Sena" }.lowercase()
    Column(
        modifier.fillMaxSize().background(headerDark).statusBarsPadding()
            .testTag("support_game_help"),
    ) {
        Row(
            Modifier.fillMaxWidth().height(56.dp).background(headerLight),
        ) {
            Box(Modifier.width(72.dp).fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                val trapezoid = when (modalityId) {
                    3 -> CaixaDSR.drawable.legacy_help_trapeze_quina
                    8 -> CaixaDSR.drawable.legacy_help_trapeze_lotofacil
                    16 -> CaixaDSR.drawable.legacy_help_trapeze_lotomania
                    18 -> CaixaDSR.drawable.legacy_help_trapeze_dupla_sena
                    19 -> CaixaDSR.drawable.legacy_help_trapeze_loteca
                    20 -> CaixaDSR.drawable.legacy_help_trapeze_timemania
                    14 -> CaixaDSR.drawable.legacy_help_trapeze_lotogol
                    else -> CaixaDSR.drawable.legacy_help_trapeze_mega_sena
                }
                Image(caixaDSLegacyDrawablePainter(trapezoid), null, Modifier.fillMaxSize())
                Image(
                    painterResource(CaixaDSR.drawable.legacy_help_trevo), null,
                    Modifier.padding(start = 16.dp, top = 16.dp).size(width = 22.dp, height = 23.dp),
                )
            }
            Row(Modifier.weight(1f).fillMaxSize()) {
                Text(
                    title,
                    Modifier.weight(.8f).padding(top = 17.dp),
                    color = lotteryStyle.content,
                    fontFamily = CaixaDSLegacyFuturaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                Box(
                    Modifier.weight(.2f).padding(start = 16.dp, top = 18.dp),
                    contentAlignment = Alignment.TopStart,
                ) {
                    Image(painterResource(if (title == "timemania") CaixaDSR.drawable.legacy_help_fechar_verde else CaixaDSR.drawable.legacy_help_btn_fechar),
                        "Fechar", Modifier.size(20.dp).focusProperties { canFocus = false }.clickable(onClick = onBack))
                }
            }
        }
        Column(
            Modifier
                .fillMaxSize()
                .background(CaixaDS.colors.legacyGraphite),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(157.dp)
                    .background(CaixaDS.colors.legacyGameHelpHeader),
            ) {
                HelpMetadata(
                    "Concurso",
                    "Nº 1456",
                    "Abertura",
                    "10/04 | 18h00",
                    Modifier.weight(1f).padding(start = 38.dp, top = 40.dp),
                )
                HelpMetadata(
                    "Próximo sorteio",
                    "09/12",
                    "Encerramento",
                    "11/04 | 15h50",
                    Modifier.weight(1f).padding(start = 38.dp, top = 40.dp),
                )
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 36.dp),
            ) {
                Text(
                    "Regras do jogo",
                    color = CaixaDS.colors.onBrand,
                    style = TextStyle(fontFamily = CaixaDSLegacyRegularFontFamily,
                        fontWeight = FontWeight.Bold, fontSize = 14.2.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = true)),
                )
                Text(
                    LEGACY_GAME_HELP_TEXT,
                    color = CaixaDS.colors.onBrand,
                    style = TextStyle(
                        fontFamily = CaixaDSLegacyRegularFontFamily,
                        fontSize = 14.2.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = true),
                        lineBreak = LineBreak.Paragraph,
                    ),
                    modifier = Modifier.padding(top = 14.2.dp, bottom = 39.dp),
                )
            }
        }
    }
}

@Composable
private fun HelpMetadata(
    firstLabel: String,
    firstValue: String,
    secondLabel: String,
    secondValue: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        val style = TextStyle(fontFamily = CaixaDSLegacyRegularFontFamily, fontSize = 14.2.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = true))
        Text(firstLabel, color = CaixaDS.colors.onBrand, style = style.copy(fontWeight = FontWeight.Bold))
        Text(firstValue, color = CaixaDS.colors.onBrand, style = style)
        Text(secondLabel, color = CaixaDS.colors.onBrand, style = style.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(top = 21.dp))
        Text(secondValue, color = CaixaDS.colors.onBrand, style = style)
    }
}

internal const val LEGACY_GAME_HELP_TEXT =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin " +
        "gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum " +
        "sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, " +
        "nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.  Lorem ipsum " +
        "dolor sit amet, consectetur adipiscing elit.  Aenean euismod bibendum laoreet. Proin gravida dolor sit " +
        "amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque " +
        "penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra " +
        "vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.Lorem ipsum dolor sit amet, consectetur " +
        "adipiscing elit. Aenean euismod bibendum laoreet.  Proin gravida dolor sit amet lacus accumsan et viverra " +
        "justo commodo.  Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient " +
        "montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, " +
        "sed rhoncus sapien nunc eget.  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod " +
        "bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales " +
        "pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. " +
        "Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.  " +
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet.  Proin gravida " +
        "dolor sit amet lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque " +
        "penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra " +
        "vulputate, felis tellus mollis orci, sed rhoncus sapien nunc eget.Lorem ipsum dolor sit amet, consectetur " +
        "adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet lacus accumsan et viverra " +
        "justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient " +
        "montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, " +
        "sed rhoncus sapien nunc eget."

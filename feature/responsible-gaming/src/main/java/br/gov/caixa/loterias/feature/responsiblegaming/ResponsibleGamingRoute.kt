package br.gov.caixa.loterias.feature.responsiblegaming

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCard
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyAttentionIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCheckbox
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyChoiceRow
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPrimaryButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRadioButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacySelfAssessmentIcon
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacySectionHeader
import br.gov.caixa.loterias.core.designsystem.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResponsibleGamingRoute(
    route: String,
    viewModel: ResponsibleGamingViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onOpenTerms: () -> Unit,
    onSubmitSuspension: (() -> Unit)? = null,
    privateDataEnabled: Boolean = true,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ResponsibleGamingEffect.NavigateBack -> onBack()
                ResponsibleGamingEffect.Completed -> onComplete()
            }
        }
    }
    ProvideTextStyle(TextStyle(fontFamily = CaixaDSFontFamily, fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true))) {
    when (route) {
        "responsible/start" -> AssessmentStartScreen(state, viewModel::onAction)
        "responsible/form" -> AssessmentFormScreen(state, viewModel::onAction, privateDataEnabled)
        "responsible/suspension" -> SuspensionScreen(
            state,
            viewModel::onAction,
            onSubmitSuspension ?: { viewModel.onAction(ResponsibleGamingAction.SubmitSuspension) },
        )
        "responsible/social" -> SocialTransfersScreen(state, viewModel::onAction, onOpenTerms)
        else -> error("Rota de jogo responsável sem layout Compose: $route")
    }
    }
}

@Composable
private fun AssessmentStartScreen(
    state: ResponsibleGamingUiState,
    onAction: (ResponsibleGamingAction) -> Unit,
) {
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White).statusBarsPadding().navigationBarsPadding()) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.horizontalGradient(listOf(CaixaDS.colors.legacyTeal, CaixaDS.colors.brand)),
            ),
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(40.dp))
                Image(
                    painter = painterResource(R.drawable.ic_loterias_caixa),
                    contentDescription = "Loterias CAIXA",
                    modifier = Modifier.width(138.dp).height(134.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(Modifier.height(20.dp))
                Image(
                    painterResource(R.drawable.ic_volantes),
                    null,
                    Modifier.width(180.dp).height(109.dp),
                    contentScale = ContentScale.Fit,
                )
                Text(
                    "Bem-vindo ao processo de\nautoavaliação Loterias CAIXA!",
                    modifier = Modifier.padding(top = 40.dp),
                    color = CaixaDSColorTokens.White,
                    fontFamily = CaixaDSLegacyRegularFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = with(LocalDensity.current) { 18.dp.toSp() },
                    textAlign = TextAlign.Center,
                )
                Text(
                    "A partir de agora, uma vez por ano, será necessário que você responda poucas e " +
                        "rápidas perguntas para que possamos entender seu perfil e orientá-lo sobre como " +
                        "evitar ou tratar, se for o caso, o desenvolvimento do transtorno do jogo compulsivo.",
                    Modifier.padding(start = 40.dp, top = 30.dp, end = 40.dp),
                    color = CaixaDSColorTokens.White,
                    fontSize = with(LocalDensity.current) { 15.dp.toSp() },
                    textAlign = TextAlign.Center,
                )
            }
            WhiteOutlineButton(
                "Seguir para autoavaliação",
                { onAction(ResponsibleGamingAction.Start) },
                Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                    .padding(start = 55.dp, end = 55.dp, bottom = 65.dp),
            )
            if (!state.assessmentRequired) {
                WhiteOutlineButton(
                    "Fechar",
                    { onAction(ResponsibleGamingAction.Back) },
                    Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                        .padding(start = 55.dp, end = 55.dp, bottom = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun AssessmentFormScreen(
    state: ResponsibleGamingUiState,
    onAction: (ResponsibleGamingAction) -> Unit,
    privateDataEnabled: Boolean = true,
) {
    CaixaDSLegacyScaffold(
        title = "Autoavaliação para o Apostador",
        onBack = null,
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.LegacyBolaoGrayStroke)) {
            when {
                state.loading -> LoadingState()
                state.errorMessage != null -> ErrorState(state.errorMessage) { onAction(ResponsibleGamingAction.Retry) }
                state.assessmentSubmitted -> AssessmentResultState(state, onAction)
                privateDataEnabled && state.currentQuestion == null -> ErrorState("Autoavaliação sem perguntas.") { onAction(ResponsibleGamingAction.Retry) }
                else -> AssessmentQuestionState(state, onAction)
            }
        }
    }
}

@Composable
private fun AssessmentQuestionState(
    state: ResponsibleGamingUiState,
    onAction: (ResponsibleGamingAction) -> Unit,
) {
            Column(
                // O CardView legado herda o fundo material #ECECEC; manter o
                // branco aqui clareava toda a ficha e reduzia a paridade visual.
                Modifier.fillMaxSize().padding(12.dp).background(CaixaDSColorTokens.LegacyMaterialCard),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp),
                ) {
                    if (!state.assessmentRequired) {
                        Row(
                            Modifier.align(Alignment.End).height(20.dp).padding(end = 4.dp)
                                .clickable { onAction(ResponsibleGamingAction.Back) },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Fechar", color = CaixaDS.colors.legacyTeal, fontSize = 17.sp)
                            androidx.compose.foundation.Image(
                                androidx.compose.ui.res.painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_assessment_close),
                                contentDescription = "Fechar",
                                modifier = Modifier.padding(start = 6.dp).size(30.dp),
                            )
                        }
                    }
                    CaixaDSLegacySelfAssessmentIcon(
                        Modifier.align(Alignment.CenterHorizontally)
                            .padding(top = if (state.assessmentRequired) 35.dp else 15.dp)
                            .width(66.dp).height(69.dp),
                    )
                    Text(
                        "Este app é válido para qualquer tipo de aposta a dinheiro, e tem como base " +
                            "os critérios de avaliação breve da prevalência de Transtorno do jogo, por meio da escala de rastreio",
                        modifier = Modifier.padding(top = 35.dp),
                        color = CaixaDS.colors.legacyPrimaryButton,
                        fontSize = 16.sp,
                    )
                    Text(
                        "NODS*-CLIP Problem Gambling Screen",
                        color = CaixaDS.colors.legacyPrimaryButton,
                        fontSize = 16.sp,
                    )
                    Text(
                        "*Volberg, R. A., Munck, I. M., & Petry, N. M. (2011). A quick and simple " +
                            "screening method for pathological and problem gamblers in addiction programs and practices. " +
                            "The American Journal on Addictions, 20, 220–227.",
                        color = CaixaDS.colors.legacyPrimaryButton,
                        fontSize = 12.sp,
                    )
                    Text(
                        state.currentQuestion?.description.orEmpty(),
                        modifier = Modifier.padding(top = 26.dp),
                        color = CaixaDS.colors.legacyPrimaryButton,
                        fontSize = 16.sp,
                    )
                    if (state.currentQuestion != null) Row(
                        Modifier.align(Alignment.CenterHorizontally).padding(top = 40.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        listOf(true to "Sim", false to "Não").forEach { (affirmative, option) ->
                            CaixaDSLegacyRadioButton(
                                selected = state.currentAnswer == affirmative,
                                onClick = { onAction(ResponsibleGamingAction.Answer(affirmative)) },
                            )
                            Text(
                                option,
                                Modifier.padding(start = 4.dp),
                                color = CaixaDS.colors.legacyPrimaryButton,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.width(10.dp))
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(12.dp)) {
                    LegacyTealButton(
                        "Anterior",
                        true,
                        { onAction(ResponsibleGamingAction.PreviousQuestion) },
                        Modifier.width(150.dp),
                        enabled = state.currentQuestionIndex > 0,
                        direction = -1,
                    )
                    Spacer(Modifier.weight(1f))
                    LegacyTealButton(
                        if (state.currentQuestionIndex == state.questions.lastIndex) "Finalizar" else "Próximo",
                        state.currentAnswer == null,
                        { onAction(ResponsibleGamingAction.NextQuestion) },
                        Modifier.width(150.dp),
                        enabled = state.currentAnswer != null,
                        direction = 1,
                    )
                }
            }
}

@Composable
private fun SuspensionScreen(
    state: ResponsibleGamingUiState,
    onAction: (ResponsibleGamingAction) -> Unit,
    onSubmitSuspension: () -> Unit,
) {
    CaixaDSLegacyScaffold(
        title = "Suspensão temporária",
        onBack = { onAction(ResponsibleGamingAction.Back) },
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding)
                .background(CaixaDSColorTokens.LegacyBolaoGrayStroke).padding(12.dp),
        ) {
            Column(
                Modifier.fillMaxSize().background(CaixaDS.colors.surface).verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (state.loading) {
                    LoadingState()
                    return@Column
                }
                if (state.errorMessage != null) {
                    ErrorState(state.errorMessage) { onAction(ResponsibleGamingAction.Retry) }
                    return@Column
                }
                if (state.suspension != null) {
                    Icon(
                        CaixaDSIcons.Confirm,
                        contentDescription = null,
                        modifier = Modifier.padding(top = 40.dp).size(58.dp),
                        tint = CaixaDS.colors.legacyTeal,
                    )
                    Text(
                        "Sua suspensão foi registrada${state.suspension.duration.takeIf(String::isNotBlank)?.let { " por $it" }.orEmpty()}.",
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        color = CaixaDS.colors.legacyPrimaryButton,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                    )
                    CaixaDSLegacyPrimaryButton(
                        "Entendi",
                        { onAction(ResponsibleGamingAction.Complete) },
                        Modifier.width(180.dp),
                    )
                    return@Column
                }
                CaixaDSLegacyAttentionIcon(
                    modifier = Modifier.padding(top = 10.dp).width(72.dp).height(89.dp),
                )
                Text(
                    "Atenção",
                    modifier = Modifier.offset(y = (-35).dp),
                    color = CaixaDS.colors.legacyPrimaryButton,
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                )
                if (state.suspensionConfirmation) {
                    Text(
                        "Confirma a suspensão das compras por ${state.selectedSuspensionPeriod}?",
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        color = CaixaDS.colors.legacyPrimaryButton,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        LegacyTealButton("Desistir", true, { onAction(ResponsibleGamingAction.Back) }, Modifier.width(130.dp))
                        LegacyTealButton("Suspender", false, onSubmitSuspension, Modifier.width(130.dp))
                    }
                    return@Column
                }
                val notices = listOf(
                    "1.Essa ação acarretará na impossibilidade de efetuar compras de apostas no Portal loterias na internet e app loterias durante o periodo selecionado",
                    "1.1 O periodo de suspensão pode ser de 24 hs, 48 hs, 7 dias, 15 dias ou 30 dias, o critério do apostador",
                    "1.2 A suspensão passa a valer a partir da confirmação da solicitação",
                    "1.3 A suspensão temporária de compras é irreversível enquanto durar o periodo selecionado",
                )
                notices.forEachIndexed { index, notice ->
                    Text(
                        notice,
                        modifier = Modifier.fillMaxWidth().padding(top = if (index == 0) 4.dp else 12.dp)
                            .offset(y = (-35).dp),
                        color = CaixaDS.colors.legacyPrimaryButton,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                    )
                }
                Text(
                    "Selecione abaixo o período de suspensão das compras",
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp).offset(y = (-35).dp),
                    color = CaixaDS.colors.legacyPrimaryButton,
                    fontSize = 14.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                )
                Column(Modifier.fillMaxWidth().padding(start = 3.dp, top = 4.dp).offset(y = (-39).dp)) {
                    listOf(listOf("24 horas", "48 horas", "7 dias"), listOf("15 dias", "30 dias")).forEach { row ->
                        Row(Modifier.fillMaxWidth()) {
                            row.forEach { option ->
                                Row(
                                    Modifier.width(120.dp).height(32.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    CaixaDSLegacyRadioButton(
                                        selected = state.selectedSuspensionPeriod == option,
                                        onClick = { onAction(ResponsibleGamingAction.SelectSuspensionPeriod(option)) },
                                    )
                                    Text(
                                        option,
                                        color = CaixaDS.colors.legacyPrimaryButton,
                                        fontSize = 13.sp,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth().height(48.dp).padding(top = 8.dp).offset(y = (-6).dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CaixaDSLegacyCheckbox(
                        checked = state.suspensionAcknowledged,
                        onClick = { onAction(ResponsibleGamingAction.ToggleSuspensionAcknowledgement) },
                        uncheckedColor = CaixaDSColorTokens.LegacySuspensionCheckboxUnchecked,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Estou ciente e concordo com as informações acima",
                        color = CaixaDS.colors.legacyPrimaryButton,
                        fontSize = 14.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    )
                }
                // No XML legado, o checkbox fica entre os RadioGroups e o
                // botão ancorado ao rodapé do cartão (margens 16dp/24dp/5dp).
                // Reproduzimos esse intervalo sem empurrar o botão para fora da tela.
                Spacer(Modifier.height(69.dp))
                LegacyTealButton(
                    "Confirmar",
                    false,
                    { onAction(ResponsibleGamingAction.ConfirmSuspension) },
                    Modifier.width(130.dp).padding(bottom = 5.dp).offset(y = (-35).dp),
                    enabled = canSubmitSuspension(state.selectedSuspensionPeriod, state.suspensionAcknowledged),
                )
            }
        }
    }
}

@Composable
private fun SocialTransfersScreen(
    state: ResponsibleGamingUiState,
    onAction: (ResponsibleGamingAction) -> Unit,
    onOpenTerms: () -> Unit,
) {
    CaixaDSLegacyScaffold(
        title = "Repasses sociais",
        onBack = { onAction(ResponsibleGamingAction.Back) },
        gradientTopBar = false,
        topBarEndColor = CaixaDS.colors.legacyHeaderEnd,
        topBarTitleSize = 20.sp,
        topBarTitleWeight = FontWeight.SemiBold,
        topBarNavigationWidth = 48.dp,
        topBarActionWidth = 65.dp,
        topBarAction = {
            Image(
                painter = painterResource(R.drawable.icon_duvida2),
                contentDescription = "Dúvidas",
                modifier = Modifier.size(25.dp).clickable(onClick = onOpenTerms),
                contentScale = ContentScale.Fit,
            )
        },
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding)) { LoadingState() }
            state.errorMessage != null -> Box(Modifier.fillMaxSize().padding(padding)) {
                ErrorState(state.errorMessage) { onAction(ResponsibleGamingAction.Retry) }
            }
            else -> SocialTransfersContent(state, padding) {
                onAction(ResponsibleGamingAction.Back)
            }
        }
    }
}

@Composable
private fun SocialTransfersContent(
    state: ResponsibleGamingUiState,
    padding: androidx.compose.foundation.layout.PaddingValues,
    onBack: () -> Unit,
) {
    val transfers = state.socialTransfers
    val transferredAmount = transfers?.amountFor(transfers.socialProgramsPercentage)
    Column(
        Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)
            .verticalScroll(rememberScrollState()),
    ) {
            Column(
                Modifier.fillMaxWidth().background(
                    Brush.horizontalGradient(listOf(CaixaDS.colors.legacyHeaderStart, CaixaDS.colors.legacyHeaderEnd)),
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Em ${currentSocialYear()}, as Loterias CAIXA já destinaram",
                    modifier = Modifier.padding(top = 40.dp),
                    color = CaixaDSColorTokens.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    formatCurrency(transferredAmount),
                    Modifier.padding(top = 15.dp),
                    color = CaixaDSColorTokens.LegacySocialLime,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "para melhorar o país.",
                    Modifier.padding(top = 18.dp, bottom = 40.dp),
                    color = CaixaDSColorTokens.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(
                Modifier.fillMaxWidth().height(130.dp).background(
                    Brush.horizontalGradient(listOf(CaixaDS.colors.legacyHeaderStart, CaixaDS.colors.legacyHeaderEnd)),
                ),
            ) {
                SocialBlock(
                    first = "Para cada",
                    value = "R$1,00",
                    description = "apostados nas Loterias\nCAIXA",
                    background = CaixaDSColorTokens.LegacySocialPanelBlue,
                    foreground = CaixaDSColorTokens.White,
                    valueColor = CaixaDSColorTokens.LegacySocialPanelLime,
                    backgroundResource = R.drawable.repassequadro1,
                    modifier = Modifier.weight(1f),
                )
                SocialBlock(
                    first = "",
                    value = "R$0,48*",
                    description = "São destinados a\nprogramas sociais",
                    background = CaixaDSColorTokens.LegacySocialPanelLime,
                    foreground = CaixaDS.colors.legacyText,
                    valueColor = CaixaDSColorTokens.LegacySocialPanelBlue,
                    backgroundResource = R.drawable.repassequadro2,
                    modifier = Modifier.weight(1f),
                )
            }
            Text(
                "*O valor inclui a parcela correspondente ao imposto de renda sobre os prêmios.",
                Modifier.fillMaxWidth().background(CaixaDS.colors.legacyFilterLine).padding(16.dp),
                color = CaixaDSColorTokens.LegacySocialGraphite,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Image(
                painterResource(R.drawable.ilustra_repasses),
                null,
                Modifier.align(Alignment.CenterHorizontally).padding(top = 40.dp).size(50.dp),
                contentScale = ContentScale.Fit,
            )
            Box(Modifier.align(Alignment.CenterHorizontally).padding(top = 15.dp).width(230.dp).height(2.dp).background(CaixaDS.colors.legacyLine))
            Text(
                "Entenda como",
                Modifier.fillMaxWidth().padding(top = 17.dp),
                color = CaixaDS.colors.brand,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                "esse dinheiro é distribuído:",
                Modifier.fillMaxWidth(),
                color = CaixaDS.colors.brand,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            SocialCategoryPair(
                left = SocialCategoryData("Educação", transfers?.let { socialAmount(it.amountFor(it.educationPercentage)) }, CaixaDSColorTokens.LegacySocialEducation),
                right = SocialCategoryData("Cultura", transfers?.let { socialAmount(it.amountFor(it.culturePercentage)) }, CaixaDSColorTokens.LegacySocialCulture),
                modifier = Modifier.padding(top = 23.dp),
            )
            SocialCategoryPair(
                left = SocialCategoryData("Esporte", transfers?.let { socialAmount(it.amountFor(it.sportPercentage)) }, CaixaDSColorTokens.LegacySocialSport),
                right = SocialCategoryData("Segurança", transfers?.let { socialAmount(it.amountFor(it.securityPercentage)) }, CaixaDSColorTokens.LegacySocialSecurity),
            )
            SocialCategoryPair(
                left = SocialCategoryData("Seguridade social", transfers?.let { socialAmount(it.amountFor(it.socialSecurityPercentage)) }, CaixaDSColorTokens.LegacySocialSecuritySystem),
                right = SocialCategoryData("Outros", transfers?.let { socialAmount(it.amountFor(it.otherPercentage)) }, CaixaDSColorTokens.LegacySocialOther),
            )
            Box(
                Modifier.fillMaxWidth().height(180.dp).background(CaixaDSColorTokens.White),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Box(Modifier.fillMaxWidth().height(100.dp).background(CaixaDSColorTokens.LegacySocialCanvas))
                Button(
                    onClick = onBack,
                    modifier = Modifier.padding(bottom = 43.dp).width(206.dp).height(52.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CaixaDSColorTokens.LegacySocialGraphite,
                        contentColor = CaixaDSColorTokens.White,
                    ),
                ) {
                    Text("Aposte e ajude ainda mais", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = CaixaDS.colors.legacyTeal)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(CaixaDSIcons.Warning, contentDescription = null, tint = CaixaDS.colors.legacyPrimaryButton)
        Text(
            message,
            modifier = Modifier.padding(top = 16.dp),
            color = CaixaDS.colors.legacyPrimaryButton,
            textAlign = TextAlign.Center,
        )
        CaixaDSLegacyPrimaryButton("Tentar novamente", onRetry, Modifier.padding(top = 20.dp).width(210.dp))
    }
}

@Composable
private fun AssessmentResultState(
    state: ResponsibleGamingUiState,
    onAction: (ResponsibleGamingAction) -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(28.dp).background(CaixaDSColorTokens.LegacyMaterialCard),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            if (state.hasAffirmativeAnswer) CaixaDSIcons.Warning else CaixaDSIcons.Confirm,
            contentDescription = null,
            modifier = Modifier.size(58.dp),
            tint = CaixaDS.colors.legacyTeal,
        )
        Text(
            if (state.hasAffirmativeAnswer) {
                "Muita atenção!\nSe você respondeu \"sim\" para uma ou mais dessas questões, recomendamos que você procure avaliação adicional."
            } else {
                "Parabéns!\nO seu hábito de apostar não está interferindo em sua vida, você é um jogador responsável."
            },
            modifier = Modifier.padding(top = 24.dp),
            color = CaixaDS.colors.legacyPrimaryButton,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        CaixaDSLegacyPrimaryButton(
            "Fechar",
            { onAction(ResponsibleGamingAction.Complete) },
            Modifier.padding(top = 28.dp).width(180.dp),
        )
    }
}

@Composable
private fun WhiteOutlineButton(text: String, onClick: () -> Unit, modifier: Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, CaixaDSColorTokens.White),
        colors = ButtonDefaults.buttonColors(containerColor = CaixaDSColorTokens.Transparent, contentColor = CaixaDSColorTokens.White),
    ) { Text(text, fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
}

@Composable
private fun LegacyTealButton(
    text: String,
    outlined: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean = true,
    direction: Int = 0,
) {
    val disabledOutline = CaixaDSColorTokens.LegacyDisabledOutline
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(4.dp),
        border = if (outlined) {
            BorderStroke(1.dp, if (enabled) CaixaDS.colors.legacyTeal else disabledOutline)
        } else {
            null
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (outlined) CaixaDS.colors.surface else CaixaDS.colors.legacyTeal,
            contentColor = if (outlined) CaixaDS.colors.legacyTeal else CaixaDSColorTokens.White,
            disabledContainerColor = if (outlined) CaixaDS.colors.surface else CaixaDSColorTokens.LegacyDisabledTealAction,
            disabledContentColor = if (outlined) disabledOutline else CaixaDSColorTokens.White,
        ),
    ) {
        if (direction < 0) {
            Icon(CaixaDSIcons.Back, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(5.dp))
        }
        Text(text)
        if (direction > 0) {
            Spacer(Modifier.width(5.dp))
            Icon(CaixaDSIcons.Forward, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SocialBlock(
    first: String,
    value: String,
    description: String,
    background: Color,
    foreground: Color,
    valueColor: Color,
    backgroundResource: Int,
    modifier: Modifier,
) {
    Box(modifier.fillMaxHeight()) {
        Image(
            painter = painterResource(backgroundResource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(background),
        )
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            if (first.isNotEmpty()) {
                Text(first, Modifier.padding(top = 18.dp), color = foreground, fontSize = 16.sp)
                Text(value, Modifier.padding(top = 4.dp), color = valueColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            } else {
                Text(value, Modifier.padding(top = 18.dp), color = valueColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                description,
                Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp),
                color = foreground,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private data class SocialCategoryData(
    val title: String,
    val amount: SocialAmount?,
    val color: Color,
)

@Composable
private fun SocialCategoryPair(
    left: SocialCategoryData,
    right: SocialCategoryData,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth().height(120.dp).background(CaixaDSColorTokens.LegacySocialCanvas)) {
        Row(Modifier.fillMaxWidth().height(76.dp)) {
            SocialCategoryAmount(left, Modifier.weight(1f))
            SocialCategoryAmount(right, Modifier.weight(1f))
        }
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            Image(
                painter = painterResource(R.drawable.icgrouprepasses),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                colorFilter = ColorFilter.tint(CaixaDSColorTokens.LegacySocialGraphite),
            )
            Row(Modifier.fillMaxSize()) {
                SocialCategoryLabel(left, Modifier.weight(1f))
                SocialCategoryLabel(right, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SocialCategoryAmount(data: SocialCategoryData, modifier: Modifier) {
    Column(modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "R$ ${data.amount?.value ?: "0,0"}",
            Modifier.padding(top = 24.dp),
            color = data.color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            data.amount?.unit ?: "reais",
            color = CaixaDSColorTokens.LegacySocialGraphite,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun SocialCategoryLabel(data: SocialCategoryData, modifier: Modifier) {
    Column(modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            data.title,
            Modifier.padding(top = 14.dp),
            color = CaixaDSColorTokens.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
        Box(Modifier.fillMaxWidth().height(5.dp).background(data.color))
    }
}

internal data class SocialAmount(val value: String, val unit: String)

internal fun socialAmount(value: BigDecimal?): SocialAmount {
    val amount = value ?: BigDecimal.ZERO
    val (divisor, singular, plural) = when {
        amount >= BigDecimal("1000000000") -> Triple(BigDecimal("1000000000"), "bilhão de reais", "bilhões de reais")
        amount >= BigDecimal("1000000") -> Triple(BigDecimal("1000000"), "milhão de reais", "milhões de reais")
        amount >= BigDecimal("1000") -> Triple(BigDecimal("1000"), "mil reais", "mil reais")
        else -> Triple(BigDecimal.ONE, "reais", "reais")
    }
    val scaled = amount.divide(divisor, 1, RoundingMode.DOWN)
    val unit = if (scaled < BigDecimal("2")) singular else plural
    return SocialAmount(scaled.toPlainString().replace('.', ','), unit)
}

internal fun currentSocialYear(): Int = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

internal fun formatCurrency(value: BigDecimal?): String = NumberFormat
    .getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    .format(value ?: BigDecimal.ZERO)
    .replace('\u00A0', ' ')
    .replace('\u202F', ' ')

internal fun formatSocialAmount(value: BigDecimal?): String {
    val amount = value ?: BigDecimal.ZERO
    val (divisor, suffix) = when {
        amount >= BigDecimal("1000000000") -> BigDecimal("1000000000") to " bi"
        amount >= BigDecimal("1000000") -> BigDecimal("1000000") to " mi"
        amount >= BigDecimal("1000") -> BigDecimal("1000") to " mil"
        else -> BigDecimal.ONE to ""
    }
    val scaled = amount.divide(divisor, 1, RoundingMode.DOWN)
    return "R$ ${scaled.toPlainString().replace('.', ',')}$suffix"
}

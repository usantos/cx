package br.gov.caixa.loterias.apostas

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.isCartSummaryVisible
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.presentation.WorkflowSpec
import br.gov.caixa.loterias.core.domain.results.DrawResult
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.responsiblegaming.SocialTransfers
import br.gov.caixa.loterias.feature.home.HomeViewModel
import br.gov.caixa.loterias.feature.home.HomeCampaign
import br.gov.caixa.loterias.feature.home.homeCampaignAt
import br.gov.caixa.loterias.feature.home.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.home.formatCompactPrize
import br.gov.caixa.loterias.feature.home.formatHomeDrawDate
import br.gov.caixa.loterias.feature.home.remainingTimeLabel
import br.gov.caixa.loterias.feature.results.ResultsUiState
import br.gov.caixa.loterias.feature.results.ResultsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.text.Normalizer
import kotlin.math.absoluteValue

data class HomeSection(val title: String, val screens: List<WorkflowSpec>)

internal enum class HomeLotteryKind { LOTTERY, INSTANT, COMBO }

internal data class HomeLottery(
    val modalityId: Int,
    val contestType: String,
    val resultCode: String,
    val name: String,
    val contestNumber: Int,
    val estimatedPrize: BigDecimal,
    val minimumBetAmount: BigDecimal,
    val drawDate: String,
    val closingDateTime: String,
    val open: Boolean,
    val kind: HomeLotteryKind,
    val color: Color,
    val darkColor: Color,
    val headerContentColor: Color,
    @DrawableRes val cloverRes: Int,
    @DrawableRes val calendarRes: Int,
    @DrawableRes val clockRes: Int,
    val octoberRoseTheme: Boolean = false,
)

private data class HomeLotteryStyle(
    val color: Color,
    val darkColor: Color,
    @DrawableRes val cloverRes: Int,
    val headerContentColor: Color = CaixaDSColorTokens.White,
    @DrawableRes val calendarRes: Int = R.drawable.icon_data_megasena,
    @DrawableRes val clockRes: Int = R.drawable.icon_horario_megasena,
)

private val homeLotteryStyles = mapOf(
    "MEGASENA" to HomeLotteryStyle(CaixaDSColorTokens.LegacyMegaSenaLight, CaixaDSColorTokens.LegacyMegaSenaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_megasena_fundo_escuro),
    "LOTOFACIL" to HomeLotteryStyle(CaixaDSColorTokens.LegacyLotofacilLight, CaixaDSColorTokens.LegacyLotofacilDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_lotofacil_fundo_escuro),
    "QUINA" to HomeLotteryStyle(CaixaDSColorTokens.LegacyQuinaLight, CaixaDSColorTokens.LegacyQuinaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_quina_fundo_escuro),
    "LOTOMANIA" to HomeLotteryStyle(CaixaDSColorTokens.LegacyLotomaniaLight, CaixaDSColorTokens.LegacyLotomaniaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_lotomania_fundo_escuro, calendarRes = R.drawable.icon_data_lotomania, clockRes = R.drawable.icon_horario_lotomania),
    "TIMEMANIA" to HomeLotteryStyle(CaixaDSColorTokens.LegacyTimemaniaLight, CaixaDSColorTokens.LegacyTimemaniaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_timemania_fundo_escuro, headerContentColor = CaixaDSColorTokens.LegacyTimemaniaContent, calendarRes = R.drawable.icon_data_timemania, clockRes = R.drawable.icon_horario_timemania),
    "DUPLASENA" to HomeLotteryStyle(CaixaDSColorTokens.LegacyDuplaSenaLight, CaixaDSColorTokens.LegacyDuplaSenaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_dupla_sena_fundo_escuro, calendarRes = R.drawable.icon_data_duplasena, clockRes = R.drawable.icon_horario_duplasena),
    "DIADESORTE" to HomeLotteryStyle(CaixaDSColorTokens.LegacyDiaDeSorteLight, CaixaDSColorTokens.LegacyDiaDeSorteDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_dia_de_sorte_fundo_escuro, CaixaDSColorTokens.LegacyDiaDeSorteContent),
    "SUPER7" to HomeLotteryStyle(CaixaDSColorTokens.LegacySuperSeteLight, CaixaDSColorTokens.LegacySuperSeteDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_super_sete_fundo_escuro, CaixaDSColorTokens.LegacySuperSeteContent),
    "MAISMILIONARIA" to HomeLotteryStyle(CaixaDSColorTokens.LegacyMaisMilionariaLight, CaixaDSColorTokens.LegacyMaisMilionariaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_milionaria_fundo_escuro),
    "LOTECA" to HomeLotteryStyle(CaixaDSColorTokens.Loteca, CaixaDSColorTokens.LotecaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevo_loteca_fundo_escuro, calendarRes = R.drawable.icon_data_loteca, clockRes = R.drawable.icon_horario_loteca),
    "LOTOGOL" to HomeLotteryStyle(CaixaDSColorTokens.Lotogol, CaixaDSColorTokens.LotogolDark, R.drawable.trevo_lotogol, calendarRes = R.drawable.icon_data_lotogol, clockRes = R.drawable.icon_horario_lotogol),
    "INSTANTANEA" to HomeLotteryStyle(CaixaDSColorTokens.LegacyInstantaneaLight, CaixaDSColorTokens.LegacyInstantaneaDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevos_instantanea_fundo_escuro),
    "COMBO" to HomeLotteryStyle(CaixaDSColorTokens.LegacyComboLight, CaixaDSColorTokens.LegacyComboDark, br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevos_combo_fundo_escuro),
)

private const val QUICK_ACCESS_ANIMATION_DURATION_MILLIS = 500
private val QuickAccessContentOffset = 404.dp
private val OctoberRoseContentOffset = 534.dp
private val OctoberRosePink = CaixaDSColorTokens.LegacyOctoberRose

@Composable
fun HomeScreen(
    userName: String?,
    networkAvailability: br.gov.caixa.loterias.core.common.NetworkAvailability,
    environmentLabel: String?,
    onOpen: (String) -> Unit,
    onPoolEntry: (br.gov.caixa.loterias.core.domain.betting.PoolEntryDecision) -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel,
    resultsViewModel: ResultsViewModel? = null,
    requestedModalityId: Int? = null,
    onProtectedAction: ((String) -> Unit)? = null,
    shakeSettings: br.gov.caixa.loterias.core.domain.betting.ShakeSettingsRepository? = null,
    onBetEntry: (br.gov.caixa.loterias.core.domain.betting.BetEntryDecision) -> Unit,
    onOtherBetting: (br.gov.caixa.loterias.core.domain.betting.HomeBettingDecision) -> Unit,
    onDrawerOpen: (String) -> Unit = onOpen,
) {
    val uriHandler = LocalUriHandler.current
    val window = LocalContext.current.findActivity()?.window
    val view = LocalView.current
    SideEffect {
        window?.let {
            WindowCompat.getInsetsController(it, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
                show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            }
        }
    }
    var quickAccessExpanded by rememberSaveable { mutableStateOf(false) }
    var octoberRoseExpanded by rememberSaveable { mutableStateOf(false) }
    val homeCampaign = remember { homeCampaignAt(LocalDate.now()) }
    var expandedResultPage by rememberSaveable { mutableStateOf<Int?>(null) }
    val homeState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleState by androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
            viewModel.refreshUnreadNotificationCount()
            if (BuildConfig.HOME_V2) {
                viewModel.load()
                viewModel.refreshCart()
            }
        }
    }
    LaunchedEffect(homeState.notificationRedirect, lifecycleState) {
        if (lifecycleState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
            homeState.notificationRedirect?.let { event ->
                if (viewModel.consumeNotificationRedirect(event)) onOpen(serviceRedirectRoute(event.destination))
            }
        }
    }
    LaunchedEffect(homeState.betEntryDecision) {
        homeState.betEntryDecision?.let {
            viewModel.consumeBetEntryDecision()
            onBetEntry(it)
        }
    }
    LaunchedEffect(viewModel) { if (!BuildConfig.HOME_V2) viewModel.refreshCart() }
    LaunchedEffect(homeState.otherBettingDecision) {
        homeState.otherBettingDecision?.let {
            viewModel.consumeOtherBettingDecision()
            onOtherBetting(it)
        }
    }
    LaunchedEffect(homeState.poolEntryDecision) {
        homeState.poolEntryDecision?.let {
            viewModel.consumePoolEntryDecision()
            onPoolEntry(it)
        }
    }
    val homeLotteries = remember(homeState.parameters, homeState.combos) {
        homeState.parameters.toHomeLotteries(
            hasCombos = homeState.combos.isNotEmpty(),
        )
    }
    val resultsState = resultsViewModel?.state?.collectAsStateWithLifecycle()?.value ?: ResultsUiState()
    val carouselOffset by animateDpAsState(
        targetValue = when {
            octoberRoseExpanded -> OctoberRoseContentOffset
            quickAccessExpanded -> QuickAccessContentOffset
            else -> 0.dp
        },
        animationSpec = tween(
            durationMillis = QUICK_ACCESS_ANIMATION_DURATION_MILLIS,
            easing = FastOutSlowInEasing,
        ),
        label = "home-carousel-offset",
    )
    val cloverRotation by animateFloatAsState(
        targetValue = if (quickAccessExpanded) 450f else 90f,
        animationSpec = tween(
            durationMillis = QUICK_ACCESS_ANIMATION_DURATION_MILLIS,
            easing = FastOutSlowInEasing,
        ),
        label = "home-clover-rotation",
    )
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val drawerOpen = drawerState.isOpen
    val statusBarInset = WindowInsets.statusBars.getTop(density).toFloat()
    val navigationBarInset = WindowInsets.navigationBars.getBottom(density).toFloat()
    if (homeState.parametersUnavailable) {
        SimulationRecoveryPanel(
            loading = homeState.loading,
            message = homeState.parameterRetryMessage,
            onRetry = viewModel::load,
            onBack = viewModel::dismissParametersUnavailable,
            onDismissMessage = viewModel::dismissParameterRetryMessage,
            networkAvailability = networkAvailability,
        )
        return
    }
    ModalNavigationDrawer(
        modifier = Modifier.drawWithContent {
            drawContent()
            if (drawerOpen) {
                // The production drawer scrim covers app content only. The Android
                // status and navigation bars stay white outside the legacy window.
                drawRect(
                    color = CaixaDSColorTokens.White,
                    topLeft = Offset.Zero,
                    size = Size(size.width, statusBarInset),
                )
                drawRect(
                    color = CaixaDSColorTokens.White,
                    topLeft = Offset(0f, size.height - navigationBarInset),
                    size = Size(size.width, navigationBarInset),
                )
            }
        },
        drawerState = drawerState,
        scrimColor = CaixaDSColorTokens.LegacyHomeDrawerScrim,
        drawerContent = {
            HomeDrawer(
                shakeSettings = shakeSettings,
                drawerOpen = drawerState.isOpen,
                features = homeState.features,
                userName = userName,
                environmentLabel = environmentLabel,
                octoberRoseTheme = homeCampaign == HomeCampaign.OUTUBRO_ROSA,
                onClose = { scope.launch { drawerState.close() } },
                onExternalFailure = viewModel::externalBettingFailed,
                onOpen = { route ->
                    scope.launch { drawerState.close() }
                    val action = homeBettingAction(route)
                    if (action != null && onProtectedAction != null) onProtectedAction("home/rapid") else if (action != null) viewModel.startOtherBetting(action) else onDrawerOpen(route)
                },
                onLogout = onLogout,
            )
        },
    ) {
    if (BuildConfig.HOME_V2) {
        val context = LocalContext.current
        val layoutPreferences = remember(context) {
            br.gov.caixa.loterias.core.preferences.SharedPreferencesHomeLayout(context)
        }
        val homeResults = resultsViewModel?.homeResults?.collectAsStateWithLifecycle()?.value.orEmpty()
        LaunchedEffect(homeLotteries) {
            resultsViewModel?.loadHomeResults(homeLotteries.map { it.resultCode }.filter {
                it in listOf("megasena", "maismilionaria", "quina")
            }, refresh = true)
        }
        Box(Modifier.fillMaxSize().statusBarsPadding().padding(
            bottom = with(density) { navigationBarInset.toDp() },
        )) {
            if (homeLotteries.isEmpty() && !homeState.loading) {
                HomeLoadingState(false, homeState.errorMessage, viewModel::load)
            } else HomeV2Content(homeLotteries, homeState.loading, homeResults, layoutPreferences,
                object : HomeV2Presentation.Actions {
                    override fun bet(modalidade: HomeLottery) {
                        if (!homeState.preparingBet) viewModel.startBet(
                            modalidade.modalityId, modalidade.contestType, modalidade.contestNumber,
                        )
                    }
                    override fun pool(modalidade: HomeLottery) {
                        if (homeState.preparingBet) return
                        if (onProtectedAction != null) onProtectedAction("home/pool/${modalidade.modalityId}/${modalidade.contestType}")
                        else viewModel.startPools(modalidade.modalityId, modalidade.contestType)
                    }
                    override fun results() = onOpen("results/modalities")
                    override fun modalityResults(modalidade: HomeV2Modality) = onOpen("results/home/${modalidade.resultCode}")
                    override fun games() = onOpen("tickets/my-bets")
                    override fun favorites() = onOpen("betting/favorites")
                    override fun menu() { scope.launch { drawerState.open() } }
                    override fun notifications() = onOpen("notifications/messages")
                    override fun cart() = onOpen("cart/current")
                    override fun profile() = menu()
                    override fun canPool(modalidade: HomeV2Modality): Boolean = homeLotteries.any {
                        it.resultCode == modalidade.resultCode && shouldShowPoolButton(
                            homeState.features.poolSalesEnabled, homeState.parameters, it,
                        )
                    }
                },
            )
        }
    } else {
    Scaffold(
        containerColor = CaixaDS.colors.legacyCanvas,
        topBar = {
            Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).statusBarsPadding()) {
                Row(
                    Modifier.fillMaxWidth().height(56.dp).padding(start = 4.dp, end = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Image(
                            painter = painterResource(R.drawable.menu),
                            contentDescription = "Abrir menu",
                            modifier = Modifier.size(width = 18.dp, height = 12.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = if (homeCampaign == HomeCampaign.OUTUBRO_ROSA) {
                                ColorFilter.tint(OctoberRosePink)
                            } else null,
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(36.dp).clickable { onOpen("notifications/messages") }.padding(4.dp),
                    ) {
                        Image(painterResource(R.drawable.installed_home_sino), "Notificações", Modifier.size(29.dp))
                        if (homeState.unreadNotificationCount > 0) Image(
                            painterResource(R.drawable.installed_home_badge_nao_lida),
                            "Notificações não lidas",
                            Modifier.size(12.dp).align(Alignment.TopEnd),
                        )
                    }
                    if (homeCampaign == HomeCampaign.OUTUBRO_ROSA) {
                        Box(
                            Modifier.size(36.dp).clickable {
                                quickAccessExpanded = false
                                expandedResultPage = null
                                octoberRoseExpanded = !octoberRoseExpanded
                            }.padding(4.dp),
                        ) {
                            Image(painterResource(R.drawable.legacy_home_outubro_rosa), "Outubro Rosa", Modifier.size(29.dp))
                        }
                    }
                    IconButton(onClick = {
                        octoberRoseExpanded = false
                        expandedResultPage = null
                        if (!quickAccessExpanded) viewModel.loadSocialTransfers()
                        quickAccessExpanded = !quickAccessExpanded
                    }) {
                        Image(
                            painter = painterResource(
                                if (homeCampaign == HomeCampaign.OUTUBRO_ROSA) R.drawable.legacy_home_icon_trevo_outubro_rosa
                                else R.drawable.icon_trevo,
                            ),
                            contentDescription = if (quickAccessExpanded) "Ocultar atalhos" else "Apresentar atalhos",
                            modifier = Modifier.size(30.dp).rotate(cloverRotation),
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }
        },
        ) { padding ->
        BoxWithConstraints(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(CaixaDSColorTokens.White, CaixaDSColorTokens.LegacyHomeGradientEnd),
                ),
            ).padding(padding),
        ) {
            if (homeLotteries.isEmpty()) {
                HomeLoadingState(homeState.loading, homeState.errorMessage, viewModel::load)
                return@BoxWithConstraints
            }
            key(homeLotteries.size) {
            val pagerState = rememberPagerState(
                initialPage = infiniteCarouselInitialPage(homeLotteries.size),
                pageCount = { Int.MAX_VALUE },
            )
            val selectedLottery = homeLotteries[loopedCarouselIndex(pagerState.currentPage, homeLotteries.size)]
            LaunchedEffect(requestedModalityId) {
                val requestedIndex = homeLotteries.indexOfFirst { it.modalityId == requestedModalityId }
                if (requestedIndex >= 0) {
                    val currentIndex = loopedCarouselIndex(pagerState.currentPage, homeLotteries.size)
                    pagerState.scrollToPage(pagerState.currentPage + requestedIndex - currentIndex)
                }
            }
            val hasCart = isCartSummaryVisible(homeState.cartTotal)
            val showPoolButton = shouldShowPoolButton(
                poolSalesEnabled = homeState.features.poolSalesEnabled,
                parameters = homeState.parameters,
                lottery = selectedLottery,
            )
            val carouselWidth = 280.dp
            val carouselHorizontalPadding = ((maxWidth - carouselWidth) / 2).coerceAtLeast(0.dp)
            AnimatedVisibility(
                visible = quickAccessExpanded,
                modifier = Modifier.align(Alignment.TopEnd),
                enter = fadeIn(tween(200, delayMillis = 160)) +
                    slideInVertically(tween(QUICK_ACCESS_ANIMATION_DURATION_MILLIS, easing = FastOutSlowInEasing)) { -it / 5 },
                exit = fadeOut(tween(200)) +
                    slideOutVertically(tween(QUICK_ACCESS_ANIMATION_DURATION_MILLIS, easing = FastOutSlowInEasing)) { -it / 5 },
            ) {
                HomeQuickAccessPanel(
                    socialTransfers = homeState.socialTransfers,
                    loadingSocialTransfers = homeState.loadingSocialTransfers,
                    onReadTicket = {
                        quickAccessExpanded = false
                        onOpen("tickets/read")
                    },
                    onResponsibleGaming = {
                        quickAccessExpanded = false
                        onOpen("responsible/start")
                    },
                    onSocialTransfers = {
                        quickAccessExpanded = false
                        onOpen("responsible/social")
                    },
                    onWomenSupport = {
                        quickAccessExpanded = false
                        runCatching {
                            uriHandler.openUri("https://api.whatsapp.com/send?phone=5561996100180&text=Disque180")
                        }
                    },
                )
            }
            AnimatedVisibility(
                visible = octoberRoseExpanded,
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                enter = fadeIn(tween(200)) + slideInVertically(tween(QUICK_ACCESS_ANIMATION_DURATION_MILLIS, easing = FastOutSlowInEasing)) { -it / 5 },
                exit = fadeOut(tween(200)) + slideOutVertically(tween(QUICK_ACCESS_ANIMATION_DURATION_MILLIS, easing = FastOutSlowInEasing)) { -it / 5 },
            ) {
                OctoberRoseCampaignPanel()
            }
            Box(Modifier.fillMaxSize().offset(y = carouselOffset)) {
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = !quickAccessExpanded && !octoberRoseExpanded && expandedResultPage == null,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 116.dp),
                    pageSize = PageSize.Fixed(carouselWidth),
                    contentPadding = PaddingValues(horizontal = carouselHorizontalPadding),
                    pageSpacing = 12.dp,
                ) { page ->
                    val distance = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                        .absoluteValue.coerceIn(0f, 1f)
                    LotteryCard(
                        lottery = homeLotteries[loopedCarouselIndex(page, homeLotteries.size)],
                        resultState = resultsState,
                        resultExpanded = expandedResultPage == page,
                        onToggleResult = {
                            if (expandedResultPage == page) {
                                expandedResultPage = null
                            } else {
                                quickAccessExpanded = false
                                expandedResultPage = page
                                resultsViewModel?.selectModality(
                                    homeLotteries[loopedCarouselIndex(page, homeLotteries.size)].resultCode,
                                )
                            }
                        },
                        modifier = Modifier.graphicsLayer {
                            // The focused card grows continuously while the pager settles. Adjacent
                            // cards remain shorter, centered vertically and partially visible.
                            scaleY = carouselCardScale(distance)
                        },
                    )
                }
                if (expandedResultPage == null) {
                    if (showPoolButton) {
                        Box(
                            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(252.dp)
                                .padding(
                                    start = 88.dp,
                                    end = 88.dp,
                                    bottom = 168.dp,
                                )
                                .padding(vertical = 6.dp)
                                .shadow(2.dp, RoundedCornerShape(4.dp))
                                .background(selectedLottery.darkColor, RoundedCornerShape(4.dp))
                                .clickable(enabled = !quickAccessExpanded && !homeState.preparingBet) {
                                    if (onProtectedAction != null) onProtectedAction("home/pool/${selectedLottery.modalityId}/${selectedLottery.contestType}")
                                    else viewModel.startPools(selectedLottery.modalityId, selectedLottery.contestType)
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "Compre seu bolão",
                                color = selectedLottery.headerContentColor,
                                style = homeLegacyTextStyle(16.sp, FontWeight.Bold),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(152.dp)
                            .padding(start = 88.dp, end = 88.dp, bottom = 68.dp)
                            .padding(vertical = 6.dp)
                            .shadow(2.dp, RoundedCornerShape(4.dp))
                            .background(CaixaDSColorTokens.White, RoundedCornerShape(4.dp))
                            .border(2.dp, if (selectedLottery.resultCode == "timemania") selectedLottery.headerContentColor else selectedLottery.darkColor, RoundedCornerShape(4.dp))
                            .clickable(enabled = !quickAccessExpanded && !homeState.preparingBet &&
                                (selectedLottery.open || selectedLottery.kind == HomeLotteryKind.LOTTERY)) {
                                when (selectedLottery.kind) {
                                    HomeLotteryKind.LOTTERY -> viewModel.startBet(
                                        selectedLottery.modalityId, selectedLottery.contestType, selectedLottery.contestNumber,
                                    )
                                    HomeLotteryKind.INSTANT -> viewModel.startOtherBetting(br.gov.caixa.loterias.core.domain.betting.HomeBettingAction.INSTANT)
                                    HomeLotteryKind.COMBO -> if (onProtectedAction != null) onProtectedAction("home/combo") else viewModel.startOtherBetting(br.gov.caixa.loterias.core.domain.betting.HomeBettingAction.COMBO)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (selectedLottery.kind == HomeLotteryKind.LOTTERY && selectedLottery.minimumBetAmount.signum() > 0) {
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Aposte") }
                                    append("\npor ${formatBrazilianCurrency(selectedLottery.minimumBetAmount)}")
                                }
                            } else {
                                buildAnnotatedString { withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Aposte") } }
                            },
                            color = if (selectedLottery.kind == HomeLotteryKind.LOTTERY && selectedLottery.resultCode != "timemania") {
                                selectedLottery.darkColor
                            } else selectedLottery.headerContentColor,
                            style = homeLegacyTextStyle(16.sp),
                            textAlign = TextAlign.Center,
                        )
                    }
                    if (hasCart) {
                        CaixaDSCartSummary(
                            total = homeState.cartTotal,
                            betCount = homeState.cartItemCount,
                            modifier = Modifier.align(Alignment.BottomCenter),
                            onClick = { if (!quickAccessExpanded) onOpen("cart/current") },
                        )
                    }
                }
            }
            }
        }
    }
    }
    }
    val showLocalCartReview = homeState.localCartReviewRequired && !homeState.localCartReviewAcknowledged &&
        !homeState.loading && !homeState.preparingBet && !homeState.parametersUnavailable
    if (showLocalCartReview) br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog(
        title = "Atenção",
        message = "Há apostas locais sem confirmação de envio. Elas foram preservadas e não serão enviadas novamente. Confira o carrinho da sua conta antes de decidir o que fazer com essas apostas.",
        positiveLabel = "Conferir apostas", negativeLabel = "Depois",
        onPositive = { viewModel.acknowledgeLocalCartReview(); onOpen("cart/local-review") },
        onNegative = { viewModel.acknowledgeLocalCartReview() },
    )
    val showPaymentNotice = !showLocalCartReview && homeState.unidentifiedPayment && !homeState.paymentNoticeAcknowledged &&
        !homeState.loading && !homeState.parametersUnavailable && !homeState.preparingBet
    if (showPaymentNotice) {
        br.gov.caixa.loterias.feature.home.HomePaymentNotice {
            if (viewModel.acknowledgeUnidentifiedPayment()) onOpen("tickets/purchases")
        }
    }
    val showNotification = !showLocalCartReview && lifecycleState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED) &&
        homeState.latestNotification != null && !homeState.unidentifiedPayment && !homeState.notificationDismissed && homeState.notificationReadError == null &&
        !homeState.loading && !homeState.parametersUnavailable && !homeState.preparingBet
    if (showNotification) {
        homeState.latestNotification?.let { notification ->
            br.gov.caixa.loterias.feature.home.HomeNotificationPopup(notification, viewModel::closeNotification) {
                viewModel.notificationLink()?.let { link ->
                    try { uriHandler.openUri(link) } catch (_: Exception) { viewModel.notificationLinkFailed() }
                }
            }
        }
    }
    homeState.notificationReadError?.let {
        br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog("Atenção", it, "Entendi", viewModel::dismissNotificationError)
    }
    homeState.notificationNotice?.let { notice ->
        br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog(
            title = "Atenção", message = notice.message, positiveLabel = notice.positiveLabel,
            onPositive = { viewModel.acceptNotificationNotice(notice) },
            negativeLabel = notice.negativeLabel, onNegative = { viewModel.dismissNotificationNotice(notice) },
            onDismiss = { viewModel.dismissNotificationNotice(notice) },
        )
    }
    homeState.betEntryError?.takeUnless { showLocalCartReview || showPaymentNotice || showNotification || homeState.notificationReadError != null || homeState.notificationNotice != null }?.let {
        br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog(
            "Atenção", it, "Entendi", viewModel::dismissBetEntryError,
        )
    }
    if (homeState.preparingBet) {
        br.gov.caixa.loterias.core.designsystem.CaixaDSAuthenticationLoadingDialog()
    }
}

@Composable
private fun HomeQuickAccessPanel(
    socialTransfers: SocialTransfers?,
    loadingSocialTransfers: Boolean,
    onReadTicket: () -> Unit,
    onResponsibleGaming: () -> Unit,
    onSocialTransfers: () -> Unit,
    onWomenSupport: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().height(QuickAccessContentOffset).padding(top = 28.dp),
        horizontalAlignment = Alignment.End,
    ) {
        HomeQuickAccessItem(
            title = "Conferir bilhetes",
            description = "Apostou em uma lotérica?\nConfira aqui os seus resultados.",
            color = CaixaDSColorTokens.LegacyQuickTicket,
            icon = {
                Image(
                    painter = painterResource(R.drawable.icon_bilhetes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            },
            onClick = onReadTicket,
        )
        HomeQuickAccessItem(
            title = "Jogo responsável",
            description = "Prevenção e tratamento aos danos relacionados\ncom jogos.",
            color = CaixaDS.colors.brand,
            icon = { AgeRestrictionIcon() },
            onClick = onResponsibleGaming,
        )
        HomeQuickAccessItem(
            title = when {
                socialTransfers != null -> formatBrazilianCurrency(socialTransfers.accumulatedRevenue)
                loadingSocialTransfers -> "Carregando..."
                else -> "Dados indisponíveis"
            },
            description = socialTransfers?.baseDate?.takeLast(4)?.takeIf(String::isNotBlank)?.let { year ->
                "repassados para boas causas em $year"
            } ?: "repassados para boas causas",
            color = CaixaDS.colors.legacyTeal,
            icon = {
                Image(
                    painter = painterResource(R.drawable.icon_repassados),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            },
            onClick = onSocialTransfers,
        )
        HomeQuickAccessItem(
            title = "Central de Atendimento à Mulher",
            description = "Denuncie e busque ajuda a vítimas de violência\npelo WhatsApp do canal 180",
            color = CaixaDSColorTokens.LegacyWomenSupport,
            icon = { WomenSupportIcon() },
            onClick = onWomenSupport,
        )
    }
}

@Composable
private fun HomeQuickAccessItem(
    title: String,
    description: String,
    color: Color,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.width(330.dp).height(86.dp).clickable(onClick = onClick),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.width(248.dp).padding(top = 4.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = title,
                color = color,
                style = homeLegacyTextStyle(14.sp, FontWeight.Bold),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = description,
                modifier = Modifier.padding(top = 2.dp),
                color = CaixaDS.colors.legacyGraphite,
                style = homeLegacyTextStyle(13.sp),
                textAlign = TextAlign.End,
            )
        }
        Box(
            modifier = Modifier.width(82.dp).height(60.dp),
            contentAlignment = Alignment.TopCenter,
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
private fun OctoberRoseCampaignPanel() {
    Box(Modifier.fillMaxWidth().height(360.dp)) {
        Image(
            painter = painterResource(R.drawable.legacy_home_marca_dagua_outubro_rosa),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterEnd).size(width = 154.dp, height = 324.dp),
            contentScale = ContentScale.FillBounds,
            alpha = 0.14f,
        )
        Column(
            Modifier.fillMaxWidth().padding(top = 42.dp, start = 48.dp, end = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Outubro Rosa",
                Modifier.fillMaxWidth().padding(end = 12.dp),
                color = OctoberRosePink,
                style = homeLegacyTextStyle(16.sp, FontWeight.Bold),
                textAlign = TextAlign.End,
            )
            Spacer(Modifier.height(18.dp))
            Text(
                buildAnnotatedString {
                    append("Campanha anual que conscientiza sobre a ")
                    withStyle(SpanStyle(color = OctoberRosePink, fontWeight = FontWeight.Bold)) {
                        append("importância da prevenção, do diagnóstico precoce e do tratamento adequado")
                    }
                    append(" do câncer de mama e do câncer do colo do útero.")
                },
                color = CaixaDSColorTokens.LegacyNotificationText,
                style = homeLegacyTextStyle(14.sp).copy(lineHeight = 20.sp),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                buildAnnotatedString {
                    append("O Ministério da Saúde orienta 3 dicas para melhorar a qualidade de vida e aumentar as chances de cura: ")
                    withStyle(SpanStyle(color = OctoberRosePink, fontWeight = FontWeight.Bold)) {
                        append("manter hábitos saudáveis, ter atenção aos sinais do corpo e fazer exames regulares.")
                    }
                },
                color = CaixaDSColorTokens.LegacyNotificationText,
                style = homeLegacyTextStyle(14.sp).copy(lineHeight = 20.sp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun WomenSupportIcon() {
    Image(painterResource(br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_ic_disque_180), null, Modifier.size(40.dp))
}

@Composable
private fun LotteryCard(
    lottery: HomeLottery,
    resultState: ResultsUiState,
    resultExpanded: Boolean,
    onToggleResult: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier.fillMaxSize(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val resultWidth by animateDpAsState(
                targetValue = if (resultExpanded) 280.dp else 240.dp,
                animationSpec = tween(500, easing = FastOutSlowInEasing),
                label = "home-result-width",
            )
            val resultHeight by animateDpAsState(
                targetValue = if (resultExpanded) (maxHeight - 103.dp).coerceAtLeast(0.dp) else 0.dp,
                animationSpec = tween(500, easing = FastOutSlowInEasing),
                label = "home-result-height",
            )
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                if (lottery.kind == HomeLotteryKind.LOTTERY) {
                    Row(
                        Modifier.width(resultWidth).height(32.dp).background(lottery.darkColor)
                            .clickable(onClick = onToggleResult),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(Modifier.width(32.dp))
                        Text(
                            "Último resultado",
                            Modifier.weight(1f),
                            color = lottery.headerContentColor,
                            style = CaixaDS.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                        Icon(
                            painterResource(if (resultExpanded) br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_ic_x_bold else br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_ic_seta_baixo),
                            contentDescription = if (resultExpanded) "Fechar resultado" else "Abrir último resultado",
                            modifier = Modifier.padding(end = 12.dp).size(9.dp),
                            tint = lottery.headerContentColor,
                        )
                    }
                    Box(
                        Modifier.width(resultWidth).height(resultHeight).background(lottery.darkColor).clipToBounds(),
                    ) {
                        HomeResultContent(resultState, lottery, Modifier.fillMaxSize())
                    }
                }
                LotteryBanner(lottery)
                Box(Modifier.weight(1f).fillMaxWidth().clipToBounds()) {
                    CollapsedLotteryContent(lottery)
                }
            }
        }
    }
}

@Composable
private fun LotteryBanner(lottery: HomeLottery) {
    Row(
        Modifier.fillMaxWidth().height(71.dp).background(lottery.color),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.width(85.dp).fillMaxSize(),
            contentAlignment = Alignment.CenterStart,
        ) {
            Image(
                painter = rememberHomeTrapezoidPainter(homeTrapezoidResource(lottery)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
            Image(
                painter = painterResource(lottery.cloverRes),
                contentDescription = null,
                modifier = Modifier.padding(start = 23.dp).size(23.dp),
                contentScale = ContentScale.Fit,
            )
        }
        Text(
            lottery.name.lowercase(java.util.Locale.forLanguageTag("pt-BR")),
            Modifier.padding(start = 5.dp),
            color = lottery.headerContentColor,
            style = homeLegacyTextStyle(if (lottery.kind == HomeLotteryKind.LOTTERY) 17.sp else 18.sp, FontWeight.Bold).copy(fontFamily = CaixaDSLegacyFuturaFontFamily),
        )
    }
}

@Composable
private fun CollapsedLotteryContent(lottery: HomeLottery) {
    if (lottery.kind != HomeLotteryKind.LOTTERY) {
        SpecialLotteryContent(lottery.kind)
        return
    }
    val remainingTime by produceState(
        initialValue = remainingTimeLabel(lottery.closingDateTime),
        key1 = lottery.closingDateTime,
    ) {
        while (true) {
            value = remainingTimeLabel(lottery.closingDateTime)
            delay(1_000)
        }
    }
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White)) {
        Image(painterResource(br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_trevos_background_card), null,
        Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
    Column(
        Modifier.fillMaxSize().padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (lottery.estimatedPrize.signum() > 0) {
            Text(formatBrazilianCurrency(lottery.estimatedPrize), Modifier.padding(top = 25.dp), color = lottery.color, style = homeLegacyTextStyle(20.sp, FontWeight.Bold))
            Text("(${formatCompactPrize(lottery.estimatedPrize)})", color = lottery.color, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, style = homeLegacyTextStyle(16.sp, FontWeight.Bold))
            Text(buildAnnotatedString { append("Prêmio estimado do concurso "); withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(lottery.contestNumber.toString()) } }, Modifier.padding(top = 3.dp), color = CaixaDSColorTokens.LegacyNotificationText, style = homeLegacyTextStyle(13.8.sp))
        } else {
            Text("Próximo concurso", Modifier.padding(top = 7.dp), color = lottery.color, style = CaixaDS.typography.titleMedium)
            Text(lottery.contestNumber.toString(), color = CaixaDS.colors.legacyText, style = CaixaDS.typography.headlineSmall)
            Text("Aguardando estimativa de prêmio", color = lottery.color, style = CaixaDS.typography.bodyMedium)
        }
        if (lottery.drawDate.isNotBlank() || lottery.closingDateTime.isNotBlank()) {
            Box(
                Modifier.fillMaxWidth().padding(top = 7.dp).height(1.dp)
                    .background(CaixaDS.colors.legacyLine),
            )
        }
        if (lottery.drawDate.isNotBlank()) {
            Row(Modifier.fillMaxWidth().padding(top = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.installed_home_calendario),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(lottery.color),
                )
                Column(Modifier.padding(start = 10.dp)) {
                    Text("Sorteio", color = CaixaDSColorTokens.LegacyNotificationText, style = homeLegacyTextStyle(12.sp))
                    Text(formatHomeDrawDate(lottery.drawDate), color = CaixaDSColorTokens.LegacyNotificationText, style = homeLegacyTextStyle(12.sp, FontWeight.SemiBold))
                }
            }
        }
        if (lottery.closingDateTime.isNotBlank()) {
            Row(Modifier.fillMaxWidth().padding(top = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(lottery.clockRes),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(lottery.color),
                )
                Column(Modifier.padding(start = 10.dp)) {
                    Text("Apostas encerram em", color = CaixaDSColorTokens.LegacyNotificationText, style = homeLegacyTextStyle(12.sp))
                    Text(remainingTime, color = CaixaDSColorTokens.LegacyNotificationText, style = homeLegacyTextStyle(12.sp, FontWeight.SemiBold))
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}
}

@Composable
private fun SpecialLotteryContent(kind: HomeLotteryKind) {
    Column(
        Modifier.fillMaxSize().background(CaixaDSColorTokens.White).padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (kind == HomeLotteryKind.INSTANT) {
            Text(
                "Diversão e prêmios na hora",
                Modifier.padding(top = 28.dp),
                color = CaixaDSColorTokens.LegacyNotificationText,
                style = homeLegacyTextStyle(13.8.sp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.imagem_instantanea),
                contentDescription = "Bilhetes da Instantânea",
                modifier = Modifier.fillMaxWidth().height(170.dp),
                contentScale = ContentScale.Fit,
            )
            Text(
                "Acesse e jogue\nno Portal da INSTANTÂNEA",
                Modifier.padding(top = 20.dp, bottom = 30.dp),
                color = CaixaDSColorTokens.LegacyNotificationText,
                style = homeLegacyTextStyle(13.8.sp),
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                "Mais que apostas: combos criados\npara aumentar suas chances\nde realizar o que você deseja.",
                Modifier.padding(top = 40.dp),
                color = CaixaDSColorTokens.LegacyNotificationText,
                style = homeLegacyTextStyle(13.8.sp),
                textAlign = TextAlign.Center,
            )
            Image(
                painter = painterResource(R.drawable.imagem_combos),
                contentDescription = "Combos de apostas",
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun HomeLoadingState(loading: Boolean, errorMessage: String?, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(color = CaixaDS.colors.brand)
        } else {
            Text(
                errorMessage ?: "Nenhuma modalidade disponível no momento.",
                color = CaixaDS.colors.legacyText,
                textAlign = TextAlign.Center,
            )
            Text(
                "Tentar novamente",
                Modifier.padding(top = 20.dp).clickable(onClick = onRetry),
                color = CaixaDS.colors.brand,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

internal fun BettingParameters?.toHomeLotteries(
    hasCombos: Boolean,
): List<HomeLottery> {
    val games = this?.games.orEmpty().mapNotNull { game ->
        val canonical = canonicalModality(game.modality.code.ifBlank { game.modality.name })
        val style = homeLotteryStyles[canonical] ?: return@mapNotNull null
        game.toHomeLottery(canonical, style)
    }
    if (!hasCombos || games.any { it.kind == HomeLotteryKind.COMBO }) return games
    val style = homeLotteryStyles.getValue("COMBO")
    return games + HomeLottery(
        modalityId = 0,
        contestType = "NORMAL",
        resultCode = "",
        name = "Combo",
        contestNumber = 0,
        estimatedPrize = BigDecimal.ZERO,
        minimumBetAmount = BigDecimal.ZERO,
        drawDate = "",
        closingDateTime = "",
        open = true,
        kind = HomeLotteryKind.COMBO,
        color = style.color,
        darkColor = style.darkColor,
        headerContentColor = style.headerContentColor,
        cloverRes = style.cloverRes,
        calendarRes = style.calendarRes,
        clockRes = style.clockRes,
    )
}

private fun LotteryGameParameters.toHomeLottery(
    canonical: String,
    defaultStyle: HomeLotteryStyle,
): HomeLottery {
    return HomeLottery(
    modalityId = modality.id,
    contestType = contestType,
    resultCode = when (canonical) {
        "MEGASENA" -> "megasena"
        "DUPLASENA" -> "duplasena"
        "DIADESORTE" -> "diadesorte"
        "SUPER7" -> "supersete"
        "MAISMILIONARIA" -> "maismilionaria"
        else -> canonical.lowercase()
    },
    name = if (contestType.equals("ESPECIAL", true) && modality.specialName.isNotBlank()) modality.specialName else modality.name,
    contestNumber = contestNumber,
    estimatedPrize = estimatedPrize,
    minimumBetAmount = prices.minOfOrNull { it.amount } ?: BigDecimal.ZERO,
    drawDate = drawDate,
    closingDateTime = closingDateTime,
    open = open,
    kind = when (canonical) {
        "INSTANTANEA" -> HomeLotteryKind.INSTANT
        "COMBO" -> HomeLotteryKind.COMBO
        else -> HomeLotteryKind.LOTTERY
    },
    // Production keeps the lottery card in its standard modality palette during campaigns.
    // Campaign styling is limited to home shortcuts and drawer decorations.
    color = defaultStyle.color,
    darkColor = defaultStyle.darkColor,
    headerContentColor = defaultStyle.headerContentColor,
    cloverRes = defaultStyle.cloverRes,
    calendarRes = defaultStyle.calendarRes,
    clockRes = defaultStyle.clockRes,
    octoberRoseTheme = false,
    )
}

internal fun shouldShowPoolButton(
    poolSalesEnabled: Boolean,
    parameters: BettingParameters?,
    lottery: HomeLottery,
): Boolean = poolSalesEnabled && lottery.open && lottery.kind == HomeLotteryKind.LOTTERY &&
    parameters?.availablePoolModalities.orEmpty().any { available ->
        available.modalityId == lottery.modalityId ||
            canonicalModality(available.modalityCode) == canonicalModality(lottery.resultCode)
    }

private fun canonicalModality(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
    .replace(Regex("\\p{Mn}+"), "")
    .filter(Char::isLetterOrDigit)
    .uppercase()

@Composable
private fun HomeResultContent(state: ResultsUiState, lottery: HomeLottery, modifier: Modifier = Modifier) {
    val errorMessage = state.errorMessage
    val result = state.result
    Box(modifier, contentAlignment = Alignment.Center) {
        when {
            state.isLoading -> androidx.compose.material3.CircularProgressIndicator(
                color = CaixaDSColorTokens.White,
                modifier = Modifier.size(32.dp),
            )
            errorMessage != null -> Text(
                errorMessage,
                Modifier.padding(24.dp),
                color = CaixaDSColorTokens.White,
                textAlign = TextAlign.Center,
            )
            result != null && normalizeModality(result.modality) == normalizeModality(lottery.resultCode) ->
                HomeDrawResult(result, Modifier.fillMaxSize())
            else -> Text(
                "Consultando último resultado...",
                color = CaixaDSColorTokens.White,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun normalizeModality(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
    .replace(Regex("\\p{Mn}+"), "")
    .filter(Char::isLetterOrDigit)
    .lowercase()

@Composable
private fun HomeDrawResult(result: DrawResult, modifier: Modifier = Modifier) {
    Column(
        modifier.verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Nº ${result.contest} | ${result.drawDate}",
            color = CaixaDSColorTokens.White,
            style = CaixaDS.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        val resultNumbers = result.firstDrawNumbers.ifEmpty { result.secondDrawNumbers }
        resultNumbers.chunked(4).forEach { row ->
            Row(
                Modifier.fillMaxWidth().padding(top = 13.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                row.forEach { number ->
                    Text(
                        number.toString().padStart(2, '0'),
                        Modifier.width(40.dp),
                        color = CaixaDSColorTokens.White,
                        style = CaixaDS.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
                repeat(4 - row.size) { Spacer(Modifier.width(40.dp)) }
            }
        }
        if (result.clovers.isNotEmpty()) {
            Text(
                "Trevos: ${result.clovers.joinToString("  ") { it.toString().padStart(2, '0') }}",
                Modifier.padding(top = 16.dp),
                color = CaixaDSColorTokens.White,
                fontWeight = FontWeight.Bold,
            )
        }
        if (result.payouts.isNotEmpty()) {
            Box(Modifier.fillMaxWidth().padding(top = 20.dp).height(1.dp).background(CaixaDSColorTokens.White.copy(alpha = .22f)))
            Text(
                "Premiados",
                Modifier.fillMaxWidth().padding(top = 16.dp),
                color = CaixaDSColorTokens.White,
                style = CaixaDS.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            result.payouts.forEach { payout ->
                Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text(payout.description, color = CaixaDSColorTokens.White, fontWeight = FontWeight.Bold)
                    Text(
                        "${payout.winners} ${if (payout.winners == 1L) "aposta ganhadora" else "apostas ganhadoras"}",
                        color = CaixaDSColorTokens.White,
                    )
                    Text("Prêmio: ${payout.value}", color = CaixaDSColorTokens.White)
                }
            }
        }
    }
}

internal fun infiniteCarouselInitialPage(itemCount: Int): Int {
    require(itemCount > 0) { "O carousel precisa ter ao menos um item" }
    val middle = Int.MAX_VALUE / 2
    return middle - middle.mod(itemCount)
}

internal fun loopedCarouselIndex(page: Int, itemCount: Int): Int {
    require(itemCount > 0) { "O carousel precisa ter ao menos um item" }
    return page.mod(itemCount)
}

internal fun carouselCardScale(distanceFromSelectedPage: Float): Float =
    1f - (distanceFromSelectedPage.coerceIn(0f, 1f) * 0.20f)

/** TextView defaults: retain font padding and let the source font determine line height. */
private fun homeLegacyTextStyle(size: TextUnit, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = CaixaDSFontFamily,
    fontSize = size,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
)

/** Original LayerDrawables preserve their20deg rotated rectangle and negative insets exactly. */
@Composable
private fun rememberHomeTrapezoidPainter(@DrawableRes resource: Int): Painter {
    return br.gov.caixa.loterias.core.designsystem.caixaDSLegacyDrawablePainter(resource)
}

@DrawableRes
private fun homeTrapezoidResource(lottery: HomeLottery): Int = when {
    lottery.kind == HomeLotteryKind.INSTANT -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_instantanea
    lottery.kind == HomeLotteryKind.COMBO -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_combo
    lottery.octoberRoseTheme -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_megasena_outubro_rosa
    else -> when (canonicalModality(lottery.resultCode)) {
        "QUINA" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_quina_mkp
        "SUPER7", "SUPERSETE" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_super_sete_mkp
        "LOTOFACIL" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_lotofacil_mkp
        "MAISMILIONARIA" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_milionaria_mkp
        "DIADESORTE" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_dia_sorte_mkp
        "LOTOMANIA" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_lotomanial_mkp
        "DUPLASENA" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_dupla_sena_mkp
        "LOTECA" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_loteca_mkp
        "TIMEMANIA" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_timemania_mkp
        "LOTOGOL" -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_lotogol
        else -> br.gov.caixa.loterias.feature.home.R.drawable.legacy_home_background_trapeze_megasena_mkp
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

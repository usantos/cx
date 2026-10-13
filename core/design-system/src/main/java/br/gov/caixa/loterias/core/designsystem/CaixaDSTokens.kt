package br.gov.caixa.loterias.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Paleta primitiva. Features devem consumir os tokens semânticos expostos por [CaixaDS]. */
object CaixaDSColorTokens {
    val LegacyAccountLimitButton = Color(0xFFD6D7D7)
    // Valores literais dos recursos legados, sem aproximação de paleta.

    val LegacyFavoriteFilterCanvas = Color(0xFFF7FAFA)
    val LegacyFavoritePickerBlue = Color(0xFF0099CC)
    val LegacySimulationMuted = Color(0xFF9EB2B8)
    val LegacySimulationBadge = Color(0xFFFDAB01)
    val LegacyDrawerInstitutionHeading = Color(0xFF3864A3)
    val LegacyHelpLotomania = Color(0xFFF99D1C)
    val LegacyHelpTimemania = Color(0xFFBEB92A)
    val LegacyHelpLoteca = Color(0xFF9F0220)
    val LegacyHelpLotogol = Color(0xFF00A8DE)
    // Valores literais dos recursos legados, sem aproximação de paleta.
    val LegacyTicketPeriodLabel = Color(0xFF005CAD)
    val LegacyComboCardName = Color(0xFF007BBC)
    val LegacyRegistrationGradientStart = Color(0xFF059AB2)
    val LegacyRegistrationGradientEnd = Color(0xFF0865B2)
    val LegacySavedCardGradientStart = Color(0xFF0974B8)
    val LegacyPurchaseStatus = Color(0xFF143DA6)
    val LegacyAccountHeading = Color(0xFF165285)
    val LegacyAccountHelper = Color(0xFFA5AAB6)
    val LegacyAccountCheckboxChecked = Color(0xFF006BB6)
    val LegacyLotomaniaMirrorBackground = Color(0xFFFDF1E6)
    val LegacyLotomaniaMirrorText = Color(0xFF4E4E4E)
    val LegacyAccountError = Color(0xFFB12B2B)
    val LegacyPixUrgent = Color(0xFFF60E0E)
    val LegacyPixWarning = Color(0xFFEAAA28)
    val LegacyPixOutline = Color(0xFF4E6178)
    val LegacyLotecaCounter = Color(0xFFC61017)
    val LegacySuperSevenHeader = Color(0xFF839F38)
    val LegacySuperSevenColumn = Color(0x80DDE89C)
    val LegacySuperSevenSelection = Color(0xFFBED730)
    val LegacySuperSevenNumber = Color(0xFF3F4B52)
    val LegacySuperSevenSelectedNumber = Color(0xFF008254)
    val LegacySavedCardGradientEnd = Color(0xFF2059AE)
    val LegacyComboDetailHeading = Color(0xFF3162A4)
    val LegacyAccountTermsText = Color(0xFF3A4859)
    val LegacyTicketPeriodSelection = Color(0xFF3E576D)
    val LegacyTicketIntro = Color(0xFF3F51B5)
    val LegacyComboDescription = Color(0xFF4F556B)
    val LegacyTicketListAction = Color(0xFF4FA7B2)
    val LegacyMercadoPagoAccent = Color(0xFF50B4E9)
    val LegacyTealAction = Color(0xFF54BBAB)
    val LegacyTicketChoiceMuted = Color(0xFF64747A)
    val LegacyAccountOutline = Color(0xFF767F8B)
    val LegacyAccountUnfocusedOutline = Color(0x6B000000)
    val LegacyRegistrationCheckBackground = Color(0xFF78D0C6)
    val LegacyComboCardBorder = Color(0xFFBCBCBC)
    val LegacyCardOutline = Color(0xFFC4C4C4)
    val LegacyTicketPeriodDivider = Color(0xFFCAE1E4)
    val LegacyTicketNoticeBadge = Color(0xFFCB2129)
    val LegacyGenderDivider = Color(0xFFD0D5DD)
    val LegacyTicketChoiceBorder = Color(0xFFD3E2E5)
    val LegacyComboGradientEnd = Color(0xFFD5DFE2)
    val LegacySimulationToolbar = Color(0xFFF0F2F2)
    val LegacyAccountDisabledField = Color(0xFFF3F5F5)
    val LegacyAccountGradientEnd = Color(0xFFF8F8F8)
    val LegacyShakeArrowBackground = Color(0xFFF8FAFA)
    val LegacyComboDetailCanvas = Color(0xFFFAFAFA)
    val LegacyAccountGradientMiddle = Color(0xFFFCFCFC)
    val LegacyRecargaAccent = Color(0xFFFE8832)

    val LegacyShakeTutorialBottom = Color(0xFFE5E5E5)
    val LegacySecondaryText = Color(0x8A000000)
    val LegacyFavoriteDivider = Color(0xFFEFF5F6)
    val LegacyLoginSwitchThumb = Color(0xFFECECEC)
    val LegacyNotificationScrim = Color(0x4D000000)
    val LegacyNotificationBlue = Color(0xFF1A7FB7)
    val LegacyNotificationAction = Color(0xFF004680)
    val Blue900 = Color(0xFF003F75)
    val Blue700 = Color(0xFF005CA9)
    val Blue500 = Color(0xFF0077C8)
    val LegacyCaixaBlue = Color(0xFF0066B3)
    val LegacyUnavailableBlue = LegacyCaixaBlue
    val Blue100 = Color(0xFFDCEEFF)
    val BlueOverlay90 = Color(0xE6005EAB)
    val Orange700 = Color(0xFFC87500)
    val Orange600 = Color(0xFFD87B00)
    val Orange500 = Color(0xFFF39200)
    val Orange100 = Color(0xFFFFE8C2)
    val YellowAction = Color(0xFFFEB21E)
    val LegacyStatusVersion = Color(0xFF58B7A6)
    val LegacyReservationWarning = Color(0xFFEAAA28)
    val LegacyReservationExpired = Color(0xFFADC0C4)
    val LegacyReservationCancelled = Color(0xFF900000)
    val BurntYellow = Color(0xFFF9B000)
    val Neutral950 = Color(0xFF111827)
    val Neutral800 = Color(0xFF1F2A37)
    val Neutral600 = Color(0xFF5F6B76)
    val Neutral300 = Color(0xFFD1D7DC)
    val Neutral200 = Color(0xFFE2E6E9)
    val Neutral100 = Color(0xFFF0F2F4)
    val Neutral050 = Color(0xFFF8F9FA)
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Transparent = Color.Transparent
    val Success700 = Color(0xFF146C43)
    val Success100 = Color(0xFFD8F3E5)
    val Warning700 = Color(0xFF8A5300)
    val Warning100 = Color(0xFFFFE8B2)
    val Error700 = Color(0xFFBA1A1A)
    val Error100 = Color(0xFFFFDAD6)
    val Info700 = Blue700
    val Info100 = Blue100
    val MegaSena = Color(0xFF209869)
    val Lotofacil = Color(0xFF930089)
    val Quina = Color(0xFF260085)
    val Lotomania = Color(0xFFF78100)
    // Cabeçalhos do carrossel inicial, preservados pixel a pixel da paleta XML anterior.
    val LegacyMegaSenaLight = Color(0xFF00A651)
    val LegacyMegaSenaDark = Color(0xFF00984A)
    val LegacyLotofacilLight = Color(0xFF91278F)
    val LegacyLotofacilDark = Color(0xFF851F82)
    val LegacyQuinaLight = Color(0xFF2E3192)
    val LegacyQuinaDark = Color(0xFF2A2A86)
    val LegacyLotomaniaLight = Color(0xFFE67200)
    val LegacyLotomaniaDark = Color(0xFFCC6500)
    val LegacyTimemaniaLight = Color(0xFFFFF200)
    val LegacyTimemaniaDark = Color(0xFFE5DB1A)
    val LegacyTimemaniaContent = Color(0xFF038141)
    val LegacyDuplaSenaLight = Color(0xFFA62B43)
    val LegacyDuplaSenaDark = Color(0xFF97263C)
    val LegacyDiaDeSorteLight = Color(0xFFE3C021)
    val LegacyDiaDeSorteDark = Color(0xFFAE9319)
    val LegacyDiaDeSorteContent = Color(0xFF7E6906)
    val LegacySuperSeteLight = Color(0xFFBED730)
    val LegacySuperSeteDark = Color(0xFF839F38)
    val LegacySuperSeteContent = Color(0xFF038141)
    val LegacyMaisMilionariaLight = Color(0xFF2A3580)
    val LegacyMaisMilionariaDark = Color(0xFF262F76)
    val LegacyPrizeDetailIcon = Color(0xFF2C3682)
    val LegacyPrizeBetNow = Color(0xFF3A485B)
    val LegacyInstantaneaLight = Color(0xFF00BBE6)
    val LegacyInstantaneaDark = Color(0xFF009CB5)
    val LegacyComboLight = Color(0xFF1577C1)
    val LegacyComboDark = Color(0xFF0066B3)
    val Timemania = Color(0xFF00A859)
    val DuplaSena = Color(0xFFA61324)
    val DiaDeSorte = Color(0xFFCB852B)
    val SuperSete = Color(0xFFA8CF45)
    val MaisMilionaria = Color(0xFF2E3078)
    val Loteca = Color(0xFFED1C24)
    val LotecaDark = Color(0xFFC61017)
    val Lotogol = Color(0xFF00B7E3)
    val LotogolDark = Color(0xFF009FC5)
    val CameraCanvas = Color(0xFF1A1A1A)
    val FavoriteBlue = Color(0xFF00B5E5)
    val LegacyRedemptionButton = Color(0xFF45C0AE)
    val LegacyMercadoPagoFooter = Color(0xFFD7E0E2)
    val LegacyPrizeGreen = Color(0xFF51B7AB)
    val LegacyPrizeUrgent = Color(0xFFFF2B2B)
    val LegacyPrizeMuted = Color(0xFF6D6E71)
    val LegacyPrizeDivider = Color(0xFFD4DFE2)
    val LegacyPrizeText = Color(0xFF2E484D)
    val LegacyQuickTicket = Color(0xFFF9765E)
    val LegacyPoolTutorialAction = Color(0xFF0164AF)
    val LegacyTutorialGreen = Color(0xFF008254)
    /** Cor observada após o tint do AppCompat no botão verde do tutorial. */
    val LegacyTutorialGreenButton = Color(0xFF00784E)
    val LegacyTutorialGray = Color(0xFF47586C)
    val LegacyTutorialCheckbox = Color(0xFF007BFF)
    val LegacyTutorialCheckboxOutline = Color(0xFFCED4DA)
    val LegacyTicketDisabledBackground = Color(0xFFEFF5F6)
    val LegacyTicketDisabledContent = Color(0xFFD7E0E2)
    val LegacyNotificationText = Color(0xFF4E596B)
    val LegacyNotificationUnreadTitle = Color(0xFF48586C)
    val LegacyNotificationIntro = Color(0xFF757575)
    val LegacyNotificationDivider = Color(0xFF48586C)
    val LegacyNotificationSwitchTrack = Color(0xFFBDBDBD)
    val LegacyNotificationSwitchThumb = Color(0xFFF1F1F1)
    val LegacyTutorialIndicator = Color(0xFF262F76)
    val LegacyTutorialIndicatorOutline = Color(0xFF252D6C)
    val LegacySuperSete = Color(0xFF80C342)
    val LegacySuperSeteIndicator = Color(0xFFBED730)
    val LegacyTutorialDivider = Color(0xFFB3C6CA)
    val LegacyWomenSupport = Color(0xFFE781A6)
    /** Cor final do degradê vertical usado na tela inicial legada. */
    val LegacyHomeGradientEnd = Color(0xFFD4DFE2)
    val LegacyNotificationSearch = Color(0xFFE5E5E5)
    val LegacyNotificationList = Color(0xFFF4F4F4)
    val LegacyNotificationReadAction = Color(0xFF2E2565)
    val LegacyNotificationDeleteAction = Color(0xFF900000)
    val LegacyNotificationResultsAction = Color(0xFF4BBDAC)
    /** Fundo externo dos cartões usado por autoavaliação/autossuspensão no app legado. */
    val LegacyBolaoGrayStroke = Color(0xFFE8E6E6)
    /** Superfície padrão do CardView Material legado (`#ECECEC`). */
    val LegacyMaterialCard = Color(0xFFECECEC)
    /** Cor efetivamente renderizada pelo botão de suspensão enquanto desabilitado. */
    val LegacyDisabledTealAction = Color(0xFF8FB5BC)
    val LegacyBurntYellow = Color(0xFFF9B000)
    val LegacySocialLime = Color(0xFFF5FEC1)
    val LegacySocialPanelBlue = Color(0xFF00437A)
    val LegacySocialPanelLime = Color(0xFFAFCA0B)
    val LegacySocialGraphite = Color(0xFF404B52)
    val LegacySocialCanvas = Color(0xFFEBF1F2)
    val LegacyDrawerChild = Color(0xFFE5F2FC)
    val LegacyDrawerDivider = Color(0xFFD0E0E3)
    val LegacyDrawerInstitutionText = Color(0xFF616971)
    val LegacySocialEducation = Color(0xFF003C4D)
    val LegacySocialCulture = Color(0xFF801905)
    val LegacySocialSport = Color(0xFF216E62)
    val LegacySocialSecurity = Color(0xFF465200)
    val LegacySocialSecuritySystem = Color(0xFF753C61)
    val LegacySocialOther = Color(0xFF664800)
    val LegacyDisabledOutline = Color(0xFFD5EEEE)
    /** Fundo não selecionado do controle segmentado do cadastro legado. */
    val LegacyRegistrationSegment = Color(0xFFEFF5F6)
    val LegacyExtendedToolbarLine = Color(0xFF00BCAC)
    val LegacyPersonalDataFooter = Color(0xFFD7E0E2)
    val LegacyPersonalDataDisabledButton = Color(0xFF8FB5BC)
    val LegacySwitchActive = Color(0xFF008254)
    val LegacyMarketplaceCanvas = Color(0xFFE8EFF1)
    val LegacyMarketplaceSelectedTab = Color(0xFF004A80)
    /** Cores do fluxo de carrinhos favoritos, extraídas diretamente do legado. */
    val LegacyFavoriteCartCanvas = Color(0xFFECECEC)
    val LegacyFavoriteCartText = Color(0xFF3964A4)
    val LegacyRetailSearchText = Color(0xFF22292E)
    val LegacyRapidFavoriteText = Color(0xFF1F2948)
    val LegacyRapidWheel = Color(0xFF51B7AB)
    val LegacyRapidExcluded = Color(0xFFF9765E)
    val LegacyRapidSummaryBackground = Color(0xFFF0F5F6)
    val LegacyFavoriteCartCard = Color(0xFF51B7AB)
    val LegacyFavoriteCartExpanded = Color(0xFF035FA9)
    /** Tons exatos dos rodapés e ações nas telas legadas de aposta/carrinho. */
    val LegacyBetFooter = Color(0xFFD4DFE2)
    val LegacyBetAction = Color(0xFF48586C)
    val LegacyBetLink = Color(0xFF3964A4)
    val LegacyBetNavigationLink = Color(0xFF0164A8)
    val LegacyBetIcon = Color(0xFF6D7B80)
    val LegacyCartIcon = Color(0xFF7D8DAA)
    val LegacyFavoriteSaved = Color(0xFF3BA99C)
    val LegacyHelpMarkSecondary = Color(0xFF8DD5B6)
}

@Immutable
data class CaixaDSColors(
    val brand: Color,
    val onBrand: Color,
    val brandStrong: Color,
    val accent: Color,
    val onAccent: Color,
    val actionHighlight: Color,
    val onActionHighlight: Color,
    val authenticationBackground: Color,
    val loadingOverlay: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val error: Color,
    val onError: Color,
    val info: Color,
    val onInfo: Color,
    /** Cores preservadas da identidade visual do aplicativo legado. */
    val legacyHeaderStart: Color,
    val legacyHeaderEnd: Color,
    val legacyCanvas: Color,
    val legacyField: Color,
    val legacyText: Color,
    val legacyMutedText: Color,
    val legacyLine: Color,
    val legacyNeptune: Color,
    val legacyGraphite: Color,
    val legacyDisabledField: Color,
    val legacyDisabledControl: Color,
    val legacyDisabledButton: Color,
    val legacyTeal: Color,
    val legacyPrimaryButton: Color,
    val legacyFilterBlue: Color,
    val legacyFilterLine: Color,
    val legacyCartDark: Color,
    val legacyFormGradientMiddle: Color,
    val legacyFormGradientEnd: Color,
    val legacySuccessGradientStart: Color,
    val legacySuccessGradientEnd: Color,
    val legacySuccessBadge: Color,
    val legacyPaymentCanvas: Color,
    val legacyPaymentCard: Color,
    val legacySavedCardAccent: Color,
    val legacyCreditCard: Color,
    val legacyCreditCardArc: Color,
    val legacyMagneticStripe: Color,
    val legacySignatureStripe: Color,
    val legacyPix: Color,
    val legacyPixDisabled: Color,
    val legacyPixCopyButton: Color,
    val legacyPaymentStatus: Color,
    val legacyGameHelpHeader: Color,
    val legacyMegaHeaderStart: Color,
    val legacyMegaHeaderEnd: Color,
)

internal val CaixaDSLightColors = CaixaDSColors(
    brand = CaixaDSColorTokens.Blue700,
    onBrand = CaixaDSColorTokens.White,
    brandStrong = CaixaDSColorTokens.Blue900,
    accent = CaixaDSColorTokens.Orange500,
    onAccent = CaixaDSColorTokens.Neutral950,
    actionHighlight = CaixaDSColorTokens.Orange600,
    onActionHighlight = CaixaDSColorTokens.White,
    authenticationBackground = CaixaDSColorTokens.Blue700,
    loadingOverlay = CaixaDSColorTokens.BlueOverlay90,
    background = CaixaDSColorTokens.White,
    surface = CaixaDSColorTokens.White,
    surfaceVariant = CaixaDSColorTokens.Neutral050,
    textPrimary = CaixaDSColorTokens.Neutral800,
    textSecondary = CaixaDSColorTokens.Neutral600,
    divider = CaixaDSColorTokens.Neutral200,
    success = CaixaDSColorTokens.Success700,
    onSuccess = CaixaDSColorTokens.White,
    warning = CaixaDSColorTokens.Warning700,
    onWarning = CaixaDSColorTokens.White,
    error = CaixaDSColorTokens.Error700,
    onError = CaixaDSColorTokens.White,
    info = CaixaDSColorTokens.Info700,
    onInfo = CaixaDSColorTokens.White,
    legacyHeaderStart = Color(0xFF0082AA),
    legacyHeaderEnd = Color(0xFF005CAB),
    legacyCanvas = Color(0xFFF7FAFA),
    legacyField = Color(0xFFF4F7F8),
    legacyText = Color(0xFF22292E),
    legacyMutedText = Color(0xFF64747A),
    legacyLine = Color(0xFFD0E0E3),
    legacyNeptune = Color(0xFF165285),
    legacyGraphite = Color(0xFF48586C),
    legacyDisabledField = Color(0xFFF3F5F5),
    legacyDisabledControl = Color(0xFF767F8B),
    legacyDisabledButton = Color(0xFFCFDFE2),
    legacyTeal = Color(0xFF54BBAB),
    legacyPrimaryButton = Color(0xFF0066B3),
    legacyFilterBlue = Color(0xFF005EA8),
    legacyFilterLine = Color(0xFFD9D9D9),
    legacyCartDark = Color(0xFF004680),
    legacyFormGradientMiddle = Color(0xFFFCFCFC),
    legacyFormGradientEnd = Color(0xFFF8F8F8),
    legacySuccessGradientStart = Color(0xFF0082AA),
    legacySuccessGradientEnd = Color(0xFF005CAB),
    legacySuccessBadge = Color(0xFF78D0C6),
    legacyPaymentCanvas = Color(0xFFF5F5F5),
    legacyPaymentCard = Color(0xFFECFAFF),
    legacySavedCardAccent = Color(0xFF50B4E9),
    legacyCreditCard = Color(0xFFDADADA),
    legacyCreditCardArc = Color(0xFFFFFFFF),
    legacyMagneticStripe = CaixaDSColorTokens.Black,
    legacySignatureStripe = Color(0xFFFCF7E6),
    legacyPix = Color(0xFF32BCAD),
    legacyPixDisabled = Color(0xFF757575),
    legacyPixCopyButton = Color(0xFFC4C4C4),
    legacyPaymentStatus = Color(0xFF52BBAA),
    legacyGameHelpHeader = Color(0xFF3A4859),
    legacyMegaHeaderStart = Color(0xFF00A651),
    legacyMegaHeaderEnd = Color(0xFF00984A),
)

internal val CaixaDSDarkColors = CaixaDSColors(
    brand = CaixaDSColorTokens.Blue500,
    onBrand = CaixaDSColorTokens.White,
    brandStrong = CaixaDSColorTokens.Blue100,
    accent = CaixaDSColorTokens.Orange500,
    onAccent = CaixaDSColorTokens.Neutral950,
    actionHighlight = CaixaDSColorTokens.Orange600,
    onActionHighlight = CaixaDSColorTokens.White,
    authenticationBackground = CaixaDSColorTokens.Blue700,
    loadingOverlay = CaixaDSColorTokens.BlueOverlay90,
    background = CaixaDSColorTokens.Neutral950,
    surface = CaixaDSColorTokens.Neutral800,
    surfaceVariant = Color(0xFF293541),
    textPrimary = CaixaDSColorTokens.White,
    textSecondary = CaixaDSColorTokens.Neutral300,
    divider = CaixaDSColorTokens.Neutral600,
    success = Color(0xFF72D6A2),
    onSuccess = CaixaDSColorTokens.Neutral950,
    warning = Color(0xFFFFC65C),
    onWarning = CaixaDSColorTokens.Neutral950,
    error = Color(0xFFFFB4AB),
    onError = CaixaDSColorTokens.Neutral950,
    info = Color(0xFF8CC8FF),
    onInfo = CaixaDSColorTokens.Neutral950,
    legacyHeaderStart = Color(0xFF0082AA),
    legacyHeaderEnd = Color(0xFF005CAB),
    legacyCanvas = CaixaDSColorTokens.Neutral950,
    legacyField = Color(0xFF293541),
    legacyText = CaixaDSColorTokens.White,
    legacyMutedText = CaixaDSColorTokens.Neutral300,
    legacyLine = CaixaDSColorTokens.Neutral600,
    legacyNeptune = Color(0xFF8CC8FF),
    legacyGraphite = CaixaDSColorTokens.Neutral300,
    legacyDisabledField = Color(0xFF293541),
    legacyDisabledControl = CaixaDSColorTokens.Neutral300,
    legacyDisabledButton = Color(0xFF45535D),
    legacyTeal = Color(0xFF78D0C6),
    legacyPrimaryButton = CaixaDSColorTokens.Blue500,
    legacyFilterBlue = Color(0xFF8CC8FF),
    legacyFilterLine = CaixaDSColorTokens.Neutral600,
    legacyCartDark = Color(0xFF003F75),
    legacyFormGradientMiddle = Color(0xFF293541),
    legacyFormGradientEnd = CaixaDSColorTokens.Neutral950,
    legacySuccessGradientStart = Color(0xFF0082AA),
    legacySuccessGradientEnd = Color(0xFF005CAB),
    legacySuccessBadge = Color(0xFF78D0C6),
    legacyPaymentCanvas = CaixaDSColorTokens.Neutral950,
    legacyPaymentCard = Color(0xFF293541),
    legacySavedCardAccent = Color(0xFF50B4E9),
    legacyCreditCard = Color(0xFF4B5563),
    legacyCreditCardArc = Color(0xFF5B6572),
    legacyMagneticStripe = CaixaDSColorTokens.Black,
    legacySignatureStripe = Color(0xFFFFDB76),
    legacyPix = Color(0xFF32BCAD),
    legacyPixDisabled = Color(0xFF757575),
    legacyPixCopyButton = Color(0xFFC4C4C4),
    legacyPaymentStatus = Color(0xFF52BBAA),
    legacyGameHelpHeader = Color(0xFF3A4859),
    legacyMegaHeaderStart = Color(0xFF00A651),
    legacyMegaHeaderEnd = Color(0xFF00984A),
)

@Immutable
data class CaixaDSSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
)

@Immutable
data class CaixaDSRadii(
    val none: Dp = 0.dp,
    val sm: Dp = 4.dp,
    val md: Dp = 8.dp,
    val lg: Dp = 16.dp,
    val pill: Dp = 999.dp,
)

@Immutable
data class CaixaDSElevation(
    val none: Dp = 0.dp,
    val low: Dp = 1.dp,
    val medium: Dp = 4.dp,
    val high: Dp = 8.dp,
)

@Immutable
data class CaixaDSSizes(
    val iconSm: Dp = 16.dp,
    val iconMd: Dp = 24.dp,
    val iconLg: Dp = 32.dp,
    val touchTarget: Dp = 48.dp,
    val controlHeight: Dp = 48.dp,
    val numberBall: Dp = 36.dp,
    val legacyTopBar: Dp = 56.dp,
    val legacyField: Dp = 52.dp,
    val legacyButton: Dp = 50.dp,
)

object CaixaDSMotion {
    const val Fast = 100
    const val Standard = 250
    const val Emphasized = 400
}

/** Compatibilidade temporária para código legado; novas telas devem usar `CaixaDS.colors`. */
@Deprecated("Use CaixaDS.colors dentro de um composable")
object CaixaColors {
    val Blue = CaixaDSColorTokens.Blue700
    val DarkBlue = CaixaDSColorTokens.Blue900
    val Orange = CaixaDSColorTokens.Orange500
    val White = CaixaDSColorTokens.White
    val Surface = CaixaDSColorTokens.Neutral050
    val Divider = CaixaDSColorTokens.Neutral200
    val Text = CaixaDSColorTokens.Neutral800
    val Error = CaixaDSColorTokens.Error700
}

package br.gov.caixa.loterias.feature.betting

internal enum class TutorialMediaKind { LOTTIE_ASSET, DRAWABLE_GIF }

internal data class TutorialPage(
    val media: String,
    val mediaKind: TutorialMediaKind,
    val speed: Float = 1f,
    val loops: Boolean = true,
    val heading: String? = null,
    val description: String? = null,
    val footer: String? = null,
)

internal data class TutorialDefinition(
    val pages: List<TutorialPage>,
    val usesLegacyOverlayControls: Boolean,
    val finalPrimaryLabel: String,
    val skipLabel: String = "Pular",
)

internal fun tutorialDefinition(route: String): TutorialDefinition = when (route) {
    "betting/tutorial" -> TutorialDefinition(
        pages = listOf(
            TutorialPage("tutorial_bolao_introducao.json", TutorialMediaKind.LOTTIE_ASSET, speed = .8f, loops = false),
            TutorialPage("tutorial_bolao_passo_2.json", TutorialMediaKind.LOTTIE_ASSET, speed = 1.2f),
            TutorialPage("tutorial_bolao_passo_4.json", TutorialMediaKind.LOTTIE_ASSET),
            TutorialPage("tutorial_bolao_passo_3.json", TutorialMediaKind.LOTTIE_ASSET),
            TutorialPage("tutorial_bolao_final.json", TutorialMediaKind.LOTTIE_ASSET, speed = .5f, loops = false),
        ),
        usesLegacyOverlayControls = true,
        finalPrimaryLabel = "Escolher\nmeu bolão",
        skipLabel = "Pular para\no final",
    )
    "betting/tutorial-plus" -> TutorialDefinition(
        pages = listOf(
            TutorialPage("tutorial_introducao_mm.json", TutorialMediaKind.LOTTIE_ASSET, speed = .8f),
            TutorialPage("tutorial_mm_passo_1.json", TutorialMediaKind.LOTTIE_ASSET, speed = 1.2f),
            TutorialPage("tutorial_mm_passo_2.json", TutorialMediaKind.LOTTIE_ASSET),
            TutorialPage("tutorial_mm_passo_3.json", TutorialMediaKind.LOTTIE_ASSET, speed = 1.2f),
            TutorialPage("tutorial_mm_passo_4.json", TutorialMediaKind.LOTTIE_ASSET),
            TutorialPage("tutorial_mm_passo_5.json", TutorialMediaKind.LOTTIE_ASSET, speed = .8f),
            TutorialPage("tutorial_mm_final.json", TutorialMediaKind.LOTTIE_ASSET),
        ),
        usesLegacyOverlayControls = true,
        finalPrimaryLabel = "Aposte agora",
    )
    "betting/tutorial-super-seven" -> TutorialDefinition(
        pages = listOf(
            TutorialPage(
                media = "super7_passo1",
                mediaKind = TutorialMediaKind.DRAWABLE_GIF,
                heading = "Bem vindo ao Super Sete",
                description = "Jogue marcando 1 número em cada uma das 7 colunas.",
            ),
            TutorialPage(
                media = "super7_passo2",
                mediaKind = TutorialMediaKind.DRAWABLE_GIF,
                heading = "Apostas combinadas",
                description = "Você pode escolher mais números por coluna fazendo apostas combinadas.",
            ),
            TutorialPage(
                media = "super7_passo3",
                mediaKind = TutorialMediaKind.DRAWABLE_GIF,
                heading = "Pronto  :)",
                description = "Agora você só precisa confirmar o seu carrinho de apostas e selecionar a opção Avançar para forma de pagamento.",
                footer = "Aumente suas chances de ganhar apostando agora mesmo!",
            ),
        ),
        usesLegacyOverlayControls = false,
        finalPrimaryLabel = "Iniciar aposta",
    )
    else -> error("Tutorial não mapeado: $route")
}

internal fun tutorialPrimaryLabel(definition: TutorialDefinition, page: Int): String? = when {
    !definition.usesLegacyOverlayControls && page == definition.pages.lastIndex -> definition.finalPrimaryLabel
    !definition.usesLegacyOverlayControls -> "Avançar"
    page == 0 -> "Vamos começar"
    page == definition.pages.lastIndex -> definition.finalPrimaryLabel
    else -> null
}

internal fun tutorialSecondaryLabel(definition: TutorialDefinition, page: Int): String =
    if (definition.usesLegacyOverlayControls && page == definition.pages.lastIndex) "Fechar" else definition.skipLabel

internal fun tutorialPageAfterPrimary(definition: TutorialDefinition, page: Int): Int? =
    if (page < definition.pages.lastIndex) page + 1 else null

internal fun tutorialPageAfterSecondary(definition: TutorialDefinition, page: Int): Int? =
    if (definition.usesLegacyOverlayControls && page < definition.pages.lastIndex) definition.pages.lastIndex else null

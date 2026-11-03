package br.gov.caixa.loterias.feature.betting.presentation.state

enum class TutorialMediaKind { LOTTIE_ASSET, DRAWABLE_GIF }

data class TutorialPage(
    val media: String,
    val mediaKind: TutorialMediaKind,
    val speed: Float = 1f,
    val loops: Boolean = true,
    val heading: String? = null,
    val description: String? = null,
    val footer: String? = null,
)

data class TutorialDefinition(
    val pages: List<TutorialPage>,
    val usesLegacyOverlayControls: Boolean,
    val finalPrimaryLabel: String,
    val skipLabel: String = "Pular",
)

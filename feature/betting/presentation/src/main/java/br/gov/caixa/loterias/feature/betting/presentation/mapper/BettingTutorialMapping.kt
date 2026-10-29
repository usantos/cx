package br.gov.caixa.loterias.feature.betting.presentation.mapper

import br.gov.caixa.loterias.core.presentation.accessibility.PortugueseSpeech
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialDefinition
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialMediaKind
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialPage

internal fun tutorialDefinition(route: String): TutorialDefinition = when (route) {
    "betting/tutorial" -> poolTutorialDefinition()
    "betting/tutorial-plus" -> plusTutorialDefinition()
    "betting/tutorial-super-seven" -> superSevenTutorialDefinition()
    else -> error("Tutorial não mapeado: $route")
}

private fun poolTutorialDefinition(): TutorialDefinition = TutorialDefinition(
    pages = listOf(
        TutorialPage(
            "tutorial_bolao_introducao.json",
            TutorialMediaKind.LOTTIE_ASSET,
            speed = .8f,
            loops = false,
            heading = "Chegaram os novos bolões da CAIXA",
            description = "Venha ver como funcionam os bolões no aplicativo Loterias CAIXA"
        ),
        TutorialPage(
            "tutorial_bolao_passo_2.json",
            TutorialMediaKind.LOTTIE_ASSET,
            speed = 1.2f,
            heading = "Navegue pelos volantes ou na lista com todos os bolões"
        ),
        TutorialPage(
            "tutorial_bolao_passo_4.json",
            TutorialMediaKind.LOTTIE_ASSET,
            heading = "Veja todos os detalhes da aposta",
            description = "Abra Detalhes para visualizar todos os jogos"
        ),
        TutorialPage(
            "tutorial_bolao_passo_3.json",
            TutorialMediaKind.LOTTIE_ASSET,
            heading = "Selecione a quantidade de cotas",
            description = "Adicione as cotas escolhidas ao carrinho de apostas"
        ),
        TutorialPage(
            "tutorial_bolao_final.json",
            TutorialMediaKind.LOTTIE_ASSET,
            speed = .5f,
            loops = false,
            heading = "Pronto",
            description = "Agora você já pode escolher sua cota de bolão preferida. Boa sorte"
        )
    ),
    usesLegacyOverlayControls = true,
    finalPrimaryLabel = messageText("escolher_meu_bolao_bff5fd"),
    skipLabel = messageText("pular_para_o_final_9a2a22")
)

private fun plusTutorialDefinition(): TutorialDefinition = TutorialDefinition(
    pages = listOf(
        TutorialPage(
            "tutorial_introducao_mm.json",
            TutorialMediaKind.LOTTIE_ASSET,
            speed = .8f,
            heading = "Chegou a Mais Milionária",
            description = "Venha ver como este novo jogo funciona"
        ),
        TutorialPage(
            "tutorial_mm_passo_1.json",
            TutorialMediaKind.LOTTIE_ASSET,
            speed = 1.2f,
            heading = "Escolha 6 números para a aposta mínima",
            description = "A ilustração mostra a seleção de números e a opção para completar com números aleatórios"
        ),
        TutorialPage(
            "tutorial_mm_passo_2.json",
            TutorialMediaKind.LOTTIE_ASSET,
            heading = "Aumente suas chances de ganhar com mais números",
            description = "No seletor de quantidade, confira as opções de 6 a 12 números e o valor de cada aposta ilustrada"
        ),
        TutorialPage(
            "tutorial_mm_passo_3.json",
            TutorialMediaKind.LOTTIE_ASSET,
            speed = 1.2f,
            heading = "Para a aposta mínima, você precisa escolher 2 trevos",
            description = "Escolha de 2 a 6 trevos para marcar nesta aposta"
        ),
        TutorialPage(
            "tutorial_mm_passo_4.json",
            TutorialMediaKind.LOTTIE_ASSET,
            heading = "Aumente suas chances de ganhar apostando com mais trevos",
            description = "Confira o valor da aposta antes de adicioná-la ao carrinho"
        ),
        TutorialPage(
            "tutorial_mm_passo_5.json",
            TutorialMediaKind.LOTTIE_ASSET,
            speed = .8f,
            heading = "Teimosinha",
            description = "Escolha em quantos concursos deseja repetir a mesma aposta"
        ),
        TutorialPage("tutorial_mm_final.json", TutorialMediaKind.LOTTIE_ASSET, heading = "Tudo pronto")
    ),
    usesLegacyOverlayControls = true,
    finalPrimaryLabel = "Aposte agora"
)

private fun superSevenTutorialDefinition(): TutorialDefinition = TutorialDefinition(
    pages = listOf(
        TutorialPage(
            media = "super7_passo1",
            mediaKind = TutorialMediaKind.DRAWABLE_GIF,
            heading = messageText("bem_vindo_ao_super_sete_ffdab6"),
            description = messageText("jogue_marcando_1_numero_em_cada_ec2a5b")
        ),
        TutorialPage(
            media = "super7_passo2",
            mediaKind = TutorialMediaKind.DRAWABLE_GIF,
            heading = "Apostas combinadas",
            description = messageText("voce_pode_escolher_mais_numeros_96770d")
        ),
        TutorialPage(
            media = "super7_passo3",
            mediaKind = TutorialMediaKind.DRAWABLE_GIF,
            heading = "Pronto  :)",
            description = messageText("agora_voce_so_precisa_confirmar_919dde"),
            footer = messageText("aumente_suas_chances_de_ganhar_a_c0a561")
        )
    ),
    usesLegacyOverlayControls = false,
    finalPrimaryLabel = "Iniciar aposta"
)

internal fun tutorialPrimaryLabel(definition: TutorialDefinition, page: Int): String? = when {
    !definition.usesLegacyOverlayControls && page == definition.pages.lastIndex -> definition.finalPrimaryLabel
    !definition.usesLegacyOverlayControls -> messageText("avancar_859e48")
    page == 0 -> messageText("vamos_comecar_ac24d9")
    page == definition.pages.lastIndex -> definition.finalPrimaryLabel
    else -> null
}

internal fun tutorialSecondaryLabel(definition: TutorialDefinition, page: Int): String =
    if (definition.usesLegacyOverlayControls && page == definition.pages.lastIndex) messageText("fechar_0f2bd8") else definition.skipLabel

internal fun tutorialPageAfterPrimary(definition: TutorialDefinition, page: Int): Int? =
    if (page < definition.pages.lastIndex) page + 1 else null

internal fun tutorialPageAfterSecondary(definition: TutorialDefinition, page: Int): Int? =
    if (definition.usesLegacyOverlayControls && page < definition.pages.lastIndex) definition.pages.lastIndex else null

/** Instruções equivalentes ao texto desenhado como contornos vetoriais nas animações incluídas. */
internal fun tutorialInstructionDescription(page: TutorialPage, index: Int, total: Int): String = listOfNotNull(
    "Etapa ${PortugueseSpeech.number(index + 1)} de ${PortugueseSpeech.number(total)}",
    page.heading,
    page.description,
    page.footer
).filter(String::isNotBlank).joinToString(". ", postfix = ".") { PortugueseSpeech.expand(it.trim().trimEnd('.', '!', '?')) }

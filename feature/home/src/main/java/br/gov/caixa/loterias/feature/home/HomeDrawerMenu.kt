package br.gov.caixa.loterias.feature.home

import br.gov.caixa.loterias.core.domain.configuration.HomeFeatureConfiguration
import java.util.Locale

/** Stable identities for expansion restoration; labels/order mirror DrawerEnum in the legacy app. */
enum class HomeDrawerItem(val label: String) {
    MY_AREA("Minha área"), PERSONAL_DATA("Dados pessoais"), MY_BETS("Minhas apostas"),
    PURCHASES("Minhas compras"), FAVORITE_BETS("Apostas favoritas"), FAVORITE_CARTS("Carrinhos favoritos"),
    CARDS("Meus cartões"), RESULTS("Resultados"), RAPID("Rapidão"), TICKETS("Conferir bilhetes"),
    RESPONSIBLE("Jogo responsável"), POLICY("Política de jogo responsável"),
    ASSESSMENT("Autoavaliação do apostador"), SUSPENSION("Suspensão temporária"),
    SOCIAL("Repasses sociais"), TERMS("Termo de uso"), QUESTIONS("Dúvidas"), ABOUT("Sobre a CAIXA Loterias"),
}

data class HomeDrawerGroup(val item: HomeDrawerItem, val children: List<HomeDrawerItem> = emptyList())

fun homeDrawerUserName(fullName: String?): String {
    val words = fullName.orEmpty().trim().split(Regex("\\s+")).filter(String::isNotEmpty)
    if (words.isEmpty()) return ""
    val locale = Locale.forLanguageTag("pt-BR")
    return listOf(words.first(), words.last()).joinToString(" ") { word ->
        word.lowercase(locale).replaceFirstChar { it.titlecase(locale) }
    }
}

fun homeDrawerMenu(features: HomeFeatureConfiguration): List<HomeDrawerGroup> = buildList {
    add(HomeDrawerGroup(HomeDrawerItem.MY_AREA, buildList {
        add(HomeDrawerItem.PERSONAL_DATA)
        if (features.myBetsVisible) add(HomeDrawerItem.MY_BETS)
        addAll(listOf(HomeDrawerItem.PURCHASES, HomeDrawerItem.FAVORITE_BETS,
            HomeDrawerItem.FAVORITE_CARTS, HomeDrawerItem.CARDS))
    }))
    add(HomeDrawerGroup(HomeDrawerItem.RESULTS))
    if (features.rapidBettingEnabled) add(HomeDrawerGroup(HomeDrawerItem.RAPID))
    add(HomeDrawerGroup(HomeDrawerItem.TICKETS))
    add(HomeDrawerGroup(HomeDrawerItem.RESPONSIBLE, listOf(HomeDrawerItem.POLICY,
        HomeDrawerItem.ASSESSMENT, HomeDrawerItem.SUSPENSION)))
    addAll(listOf(HomeDrawerItem.SOCIAL, HomeDrawerItem.TERMS, HomeDrawerItem.QUESTIONS,
        HomeDrawerItem.ABOUT).map(::HomeDrawerGroup))
}

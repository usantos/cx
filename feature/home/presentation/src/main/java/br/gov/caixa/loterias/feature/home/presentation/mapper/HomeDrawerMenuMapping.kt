package br.gov.caixa.loterias.feature.home.presentation.mapper

import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import java.util.Locale
import br.gov.caixa.loterias.feature.home.presentation.state.HomeDrawerGroup
import br.gov.caixa.loterias.feature.home.presentation.state.HomeDrawerItem

fun homeDrawerUserName(fullName: String?): String {
    val words = fullName.orEmpty().trim().split(Regex("\\s+")).filter(String::isNotEmpty)
    if (words.isEmpty()) return ""
    val locale = Locale.forLanguageTag("pt-BR")
    return listOf(words.first(), words.last()).joinToString(" ") { word ->
        word.lowercase(locale).replaceFirstChar { it.titlecase(locale) }
    }
}

fun homeDrawerMenu(features: HomeFeatureConfiguration): List<HomeDrawerGroup> = buildList {
    add(
        HomeDrawerGroup(
            HomeDrawerItem.MY_AREA,
            buildList {
                add(HomeDrawerItem.PERSONAL_DATA)
                if (features.myBetsVisible) add(HomeDrawerItem.MY_BETS)
                addAll(
                    listOf(
                        HomeDrawerItem.PURCHASES,
                        HomeDrawerItem.FAVORITE_BETS,
                        HomeDrawerItem.FAVORITE_CARTS,
                        HomeDrawerItem.FAVORITE_RETAILERS,
                        HomeDrawerItem.CARDS
                    )
                )
            }
        )
    )
    add(HomeDrawerGroup(HomeDrawerItem.RESULTS))
    if (features.rapidBettingEnabled) add(HomeDrawerGroup(HomeDrawerItem.RAPID))
    add(HomeDrawerGroup(HomeDrawerItem.TICKETS))
    add(
        HomeDrawerGroup(
            HomeDrawerItem.RESPONSIBLE,
            listOf(
                HomeDrawerItem.POLICY,
                HomeDrawerItem.ASSESSMENT,
                HomeDrawerItem.SUSPENSION
            )
        )
    )
    addAll(
        listOf(
            HomeDrawerItem.SOCIAL,
            HomeDrawerItem.TERMS,
            HomeDrawerItem.QUESTIONS,
            HomeDrawerItem.ABOUT
        ).map(::HomeDrawerGroup)
    )
}

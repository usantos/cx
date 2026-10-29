package br.gov.caixa.loterias.core.navigation.destination

/** Ordem legada do RateUtils, sem alterar a pilha de navegação se nenhuma loja puder ser aberta. */
fun openAppStore(packageName: String, open: (String) -> Boolean): Boolean =
    open("market://details?id=$packageName") || open("https://play.google.com/store/apps/details?id=$packageName")

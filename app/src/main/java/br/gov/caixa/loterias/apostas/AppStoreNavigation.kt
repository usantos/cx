package br.gov.caixa.loterias.apostas

/** Legacy RateUtils order, without changing the navigation stack if no store can be opened. */
internal fun openAppStore(packageName: String, open: (String) -> Boolean): Boolean =
    open("market://details?id=$packageName") || open("https://play.google.com/store/apps/details?id=$packageName")

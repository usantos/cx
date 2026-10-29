package br.gov.caixa.loterias.core.navigation.workflow

object LoginNavigation {
    const val pattern = "account/login?authorize={authorize}&silent={silent}"
    const val authorize = "account/login?authorize=true"
    const val SILENT = "account/login?silent=true"
}

package br.gov.caixa.loterias.core.domain.configuration.model

/** Preserva capacidades existentes quando o JSON antigo ainda contém somente FAQ. */
data class AppModulesConfiguration(val enabledModules: Set<AppModule> = AppModule.entries.filterTo(linkedSetOf()) { it.defaultEnabled }) {
    fun isEnabled(module: AppModule): Boolean = module in enabledModules
}

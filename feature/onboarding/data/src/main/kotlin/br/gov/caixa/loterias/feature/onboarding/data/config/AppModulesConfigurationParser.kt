package br.gov.caixa.loterias.feature.onboarding.data.config

import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.Strictness

/** Rejeita o documento inválido para preservar a última configuração válida. */
internal fun parseAppModulesConfiguration(value: String): AppModulesConfiguration? = runCatching {
    val root = modulesGson.fromJson(value, JsonObject::class.java)
    val version = root.get("schema_version")
    require(version?.isJsonPrimitive == true && version.asJsonPrimitive.isNumber && version.asString == "1")
    val modules = root.getAsJsonObject("modules") ?: error("Módulos ausentes")
    val enabled = AppModule.entries.filterTo(linkedSetOf()) { module ->
        val entry = modules.get(module.key) ?: return@filterTo module.defaultEnabled
        val enabledValue = entry.asJsonObject.get("enabled") ?: error("Disponibilidade ausente")
        require(enabledValue.isJsonPrimitive && enabledValue.asJsonPrimitive.isBoolean)
        enabledValue.asBoolean
    }
    AppModulesConfiguration(enabled)
}.getOrNull()

private val modulesGson = GsonBuilder().setStrictness(Strictness.STRICT).create()

package br.gov.caixa.loterias.feature.onboarding.data.config

import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconCampaign
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconSchedule
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.Strictness
import java.time.MonthDay
import java.time.ZoneId

internal fun parseLauncherIconSchedule(value: String): LauncherIconSchedule = runCatching {
    val root = scheduleGson.fromJson(value, JsonObject::class.java)
    val timezone = root.get("timezone")?.let { ZoneId.of(it.strictString() ?: error("Fuso inválido")) }
        ?: ZoneId.of("America/Sao_Paulo")
    val campaigns = root.get("campaigns")?.asJsonArray?.mapNotNull { entry ->
        runCatching { parseCampaign(entry.asJsonObject) }.getOrNull()
    }.orEmpty()
    LauncherIconSchedule(timezone, campaigns)
}.getOrElse { LauncherIconSchedule() }

private fun parseCampaign(entry: JsonObject): LauncherIconCampaign {
    val icon = LauncherIconPreset.entries.firstOrNull { it.configValue == entry.get("icon")?.strictString() }
        ?: error("Ícone desconhecido")
    return LauncherIconCampaign(icon, parseMonthDay(entry.get("start")), parseMonthDay(entry.get("end")))
}

private fun parseMonthDay(value: JsonElement?): MonthDay {
    val text = value?.strictString() ?: error("Data ausente")
    require(Regex("[0-9]{2}-[0-9]{2}").matches(text)) { "Data inválida" }
    return MonthDay.parse("--$text")
}

private fun JsonElement.strictString(): String? = takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }?.asString

private val scheduleGson = GsonBuilder().setStrictness(Strictness.STRICT).create()

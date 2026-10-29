package br.gov.caixa.loterias.feature.onboarding.domain.model

import java.text.Normalizer
import java.util.Locale

enum class LauncherIconPreset(val configValue: String) {
    DEFAULT("default"),
    OUTUBRO_ROSA("outubro_rosa"),
    SETEMBRO_AMARELO("setembro_amarelo"),
    NOVEMBRO_AZUL("novembro_azul"),
    AGOSTO_LILAS("agosto_lilas"),
    ;

    companion object {
        fun fromConfig(value: String?): LauncherIconPreset {
            val normalized = Normalizer.normalize(value.orEmpty().trim(), Normalizer.Form.NFD)
                .replace(Regex("\\p{M}+"), "")
                .lowercase(Locale.ROOT)
                .replace(Regex("[\\s-]+"), "_")
            return entries.firstOrNull { it.configValue == normalized } ?: DEFAULT
        }
    }
}

package br.gov.caixa.loterias.feature.marketplace.domain.model

import br.gov.caixa.loterias.feature.marketplace.domain.model.apimessages.localizedMessage as messageText
import java.util.Locale

data class MarketplaceSpecialContests(val megaThirtyYearsContest: Int? = null, val lotecaFathersDayContest: Int? = null) {
    fun label(modality: String, contestType: Int, contestNumber: Int): String? {
        if (contestType != 2 || contestNumber <= 0) return null
        return when (modality.uppercase(Locale.ROOT).replace('-', '_').replace(' ', '_')) {
            "MEGA_SENA", "MEGASENA" -> if (contestNumber == megaThirtyYearsContest) "mega-sena\n30 anos" else null
            "LOTECA" -> if (contestNumber == lotecaFathersDayContest) messageText("loteca_dos_pais_f69a3a") else null
            else -> null
        }
    }
}

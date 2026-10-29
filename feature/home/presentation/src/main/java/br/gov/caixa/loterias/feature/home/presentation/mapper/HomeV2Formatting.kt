package br.gov.caixa.loterias.feature.home.presentation.mapper

import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText
import java.math.BigDecimal
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale

object HomeV2Formatting {
    private val locale = Locale.forLanguageTag("pt-BR")
    fun prize(value: BigDecimal?): String {
        if (value == null) return messageText("final_prize_pending")
        val million = BigDecimal("1000000")
        val thousand = BigDecimal("1000")
        fun number(n: BigDecimal) = n.stripTrailingZeros().toPlainString().replace('.', ',')
        return when {
            value >= million -> "R$ ${number(value.divide(million))} ${if (value == million) {
                messageText("milhao_e71117")
            } else {
                messageText(
                    "milhoes_ae1862"
                )
            }}"

            value >= thousand -> "R$ ${number(value.divide(thousand))} mil"

            else -> "R$ ${number(value)}"
        }
    }
    fun date(raw: String?): String {
        if (raw.isNullOrBlank()) return messageText("final_draw_pending")
        for (pattern in listOf(
            "yyyy-MM-dd'T'HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "dd/MM/yyyy",
            "yyyy-MM-dd"
        )) {
            val position = ParsePosition(0)
            val parsed = SimpleDateFormat(pattern, locale).apply { isLenient = false }.parse(raw, position)
            if (parsed == null || position.index != raw.length) continue
            val output = if (!pattern.contains("HH")) {
                "dd/MM"
            } else {
                val minutes = SimpleDateFormat("mm", locale).format(parsed)
                if (minutes == "00") "EEE • HH'h'" else "EEE • HH'h'mm"
            }
            return SimpleDateFormat(output, locale).format(parsed)
        }
        return messageText("final_draw_pending")
    }
}

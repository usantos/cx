package br.gov.caixa.loterias.core.presentation.accessibility

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

/** Formatação pura de apresentação compartilhada por descrições acessíveis; sem motor TTS nem efeitos de domínio. */
object PortugueseSpeech {
    fun number(value: Int, feminine: Boolean = false): String = integer(BigInteger.valueOf(value.toLong()), feminine)

    fun integer(value: BigInteger, feminine: Boolean = false): String {
        if (value.signum() == 0) return UNITS[0]
        if (value.signum() < 0) return "menos " + integer(value.negate(), feminine)
        val groups = value.toString().reversed().chunked(GROUP_DIGITS).map { it.reversed().toInt() }.reversed()
        if (groups.size > SCALES.size) return identifier(value.toString())
        val nonzero = groups.mapIndexedNotNull { index, group ->
            if (group == 0) null else (groups.lastIndex - index) to group
        }
        return buildString {
            nonzero.forEachIndexed { index, (rank, group) ->
                if (index > 0) {
                    val finalGroup = index == nonzero.lastIndex
                    append(if (finalGroup && (group < HUNDRED || group % HUNDRED == 0)) " e " else " ")
                }
                when (rank) {
                    0 -> append(belowThousand(group, feminine))

                    1 -> {
                        if (group != 1) append(belowThousand(group, feminine) + " ")
                        append("mil")
                    }

                    else -> append(belowThousand(group, false) + " " + if (group == 1) SCALES[rank].first else SCALES[rank].second)
                }
            }
        }
    }

    fun currency(value: BigDecimal): String {
        val amount = value.setScale(CURRENCY_SCALE, RoundingMode.HALF_EVEN)
        val absolute = amount.abs()
        val reais = absolute.toBigInteger()
        val cents = absolute.remainder(BigDecimal.ONE).movePointRight(CURRENCY_SCALE).toInt()
        val parts = buildList {
            if (reais != BigInteger.ZERO || cents == 0) {
                val unit = when {
                    reais == BigInteger.ONE -> "real"
                    reais >= MILLION && reais.mod(MILLION) == BigInteger.ZERO -> "de reais"
                    else -> "reais"
                }
                add("${integer(reais)} $unit")
            }
            if (cents != 0) add("${number(cents)} ${if (cents == 1) "centavo" else "centavos"}")
        }
        return (if (amount.signum() < 0) "menos " else "") + parts.joinToString(" e ")
    }

    fun expand(value: String): String {
        val ordinals = ORDINAL_DIGITS.replace(value) { match ->
            val index = match.groupValues[1].toIntOrNull()
            val name = ORDINALS.getOrNull(index?.minus(1) ?: -1)
            if (name == null) {
                "posição ${integer(match.groupValues[1].toBigInteger())}"
            } else if (match.value.endsWith('ª')) {
                name.dropLast(1) + "a"
            } else {
                name
            }
        }
        return DIGITS.replace(ordinals.replace("Conc.", "Concurso")) { integer(it.value.toBigInteger()) }
    }

    fun date(value: String): String {
        val trimmed = value.trim()
        val date = namedDate(trimmed) ?: run {
            val formatter = when {
                BRAZILIAN_DATE.matches(trimmed) -> BRAZILIAN_DATE_FORMAT
                ISO_DATE.matches(trimmed) -> DateTimeFormatter.ISO_LOCAL_DATE
                else -> return expand(value)
            }
            try {
                LocalDate.parse(trimmed, formatter)
            } catch (_: DateTimeParseException) {
                return expand(value)
            }
        }
        val day = if (date.dayOfMonth == 1) "primeiro" else number(date.dayOfMonth)
        return "$day de ${MONTHS[date.monthValue - 1]} de ${number(date.year)}"
    }

    private fun namedDate(value: String): LocalDate? {
        val parts = NAMED_DATE.matchEntire(value)?.groupValues ?: return null
        val month = parts[2].lowercase(java.util.Locale.ROOT).removeSuffix(".")
        val index = MONTHS.indexOf(month).takeIf { it >= 0 } ?: MONTH_ABBREVIATIONS.indexOf(month)
        if (index < 0) return null
        return try {
            LocalDate.of(parts[YEAR_COMPONENT_INDEX].toInt(), index + 1, parts[1].toInt())
        } catch (_: DateTimeException) {
            null
        }
    }

    /** Identificadores preservam zeros à esquerda e não convertem um código opaco em uma quantidade cardinal. */
    fun identifier(value: String): String = value.map { if (it.isDigit()) number(it.digitToInt()) else it.toString() }.joinToString(" ")

    private fun belowThousand(value: Int, feminine: Boolean): String {
        if (value < TEENS.size) {
            return when {
                value == 1 && feminine -> "uma"
                value == 2 && feminine -> "duas"
                else -> TEENS[value]
            }
        }
        if (value < HUNDRED) {
            val tens = TENS[value / TEN]
            return tens + if (value % TEN == 0) "" else " e " + belowThousand(value % TEN, feminine)
        }
        if (value == HUNDRED) return "cem"
        val hundred = HUNDREDS[value / HUNDRED].let {
            if (feminine && value / HUNDRED > 1) it.removeSuffix("os") + "as" else it
        }
        return hundred + if (value % HUNDRED == 0) "" else " e " + belowThousand(value % HUNDRED, feminine)
    }
}

private const val TEN = 10
private const val HUNDRED = 100
private const val GROUP_DIGITS = 3
private const val CURRENCY_SCALE = 2
private val MILLION = BigInteger("1000000")
private val DIGITS = Regex("\\d+")
private val UNITS = listOf("zero", "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove")
private val TEENS = UNITS + listOf("dez", "onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete", "dezoito", "dezenove")
private val TENS = listOf("", "", "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa")
private val HUNDREDS =
    listOf("", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos", "seiscentos", "setecentos", "oitocentos", "novecentos")
private val SCALES = listOf(
    "" to "",
    "mil" to "mil",
    "milhão" to "milhões",
    "bilhão" to "bilhões",
    "trilhão" to "trilhões",
    "quatrilhão" to "quatrilhões",
    "quintilhão" to "quintilhões"
)

private val ORDINAL_DIGITS = Regex("(\\d+)[ºª]")
private val ORDINALS = listOf("primeiro", "segundo", "terceiro", "quarto", "quinto", "sexto", "sétimo", "oitavo", "nono", "décimo")
private val MONTHS =
    listOf("janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro")
private val BRAZILIAN_DATE = Regex("\\d{2}/\\d{2}/\\d{4}")
private val ISO_DATE = Regex("\\d{4}-\\d{2}-\\d{2}")
private val BRAZILIAN_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)

private val NAMED_DATE = Regex("(\\d{1,2})\\s+([\\p{L}.]+)\\s+(\\d{4})")
private val MONTH_ABBREVIATIONS = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")

private const val YEAR_COMPONENT_INDEX = 3

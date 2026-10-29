package br.gov.caixa.loterias.feature.betting.domain.recovery

/** Somente primitivas com limites; falhas de esquema devem propagar em vez de virar um rascunho vazio. */
object BettingCheckpointCodec {
    private const val FIELD_COUNT = 22
    private const val MAX_DOCUMENT_LENGTH = 16_384
    private const val MAX_MATCHES = 32
    private const val SCORE_FIELDS = 3
    private const val MAX_LIST_SIZE = 100

    fun encode(value: BettingCheckpoint): Map<String, String> {
        value.validate()
        return linkedMapOf(
            "version" to "1", "modality" to value.modalityId.toString(), "contestType" to value.contestType,
            "contest" to value.contestNumber.toString(), "mode" to value.mode, "step" to value.step,
            "numbers" to value.numbers.sorted().joinToString(","), "clovers" to value.clovers.sorted().joinToString(","),
            "team" to value.teamNumber.optional(), "query" to value.teamQuery, "month" to value.monthNumber.optional(),
            "loteca" to
                value.loteca.toSortedMap().entries.joinToString(";") { (match, outcomes) ->
                    "$match:${outcomes.sorted().joinToString(",")}"
                },
            "lotogol" to
                value.lotogol.toSortedMap().entries.joinToString(";") { (match, score) ->
                    "$match:${score.first.optional()}:${score.second.optional()}"
                },
            "superSeven" to value.superSeven.joinToString(";") { it.sorted().joinToString(",") },
            "mirror" to value.mirror.toString(), "quantity" to value.numberQuantity.toString(),
            "recurring" to value.recurringDraws.toString(), "surpriseQuantity" to value.surpriseQuantity.toString(),
            "surpriseClovers" to value.surpriseCloverQuantity.toString(), "manualClovers" to value.manualCloverQuantity.toString(),
            "favoriteName" to value.favoriteName, "favoriteExpanded" to value.favoriteExpanded.toString()
        )
    }

    fun decode(values: Map<String, String>): BettingCheckpoint {
        require(values.size == FIELD_COUNT && values.values.sumOf { it.length } <= MAX_DOCUMENT_LENGTH)
        fun field(key: String): String = requireNotNull(values[key])
        fun integer(key: String) = field(key).toInt()
        require(field("version") == "1")
        fun entries(key: String): List<List<String>> = field(key).takeIf { it.isNotEmpty() }
            ?.split(';')?.also { require(it.size <= MAX_MATCHES) }?.map { it.split(':') }.orEmpty()
        val lotecaEntries = entries("loteca")
        require(lotecaEntries.all { it.size == 2 })
        val loteca = lotecaEntries.associate { it[0].toInt() to it[1].takeIf(String::isNotEmpty)?.split(',')?.toSet().orEmpty() }
        require(loteca.size == lotecaEntries.size)
        val lotogolEntries = entries("lotogol")
        require(lotogolEntries.all { it.size == SCORE_FIELDS })
        val lotogol = lotogolEntries.associate { it[0].toInt() to (it[1].optionalInt() to it[2].optionalInt()) }
        require(lotogol.size == lotogolEntries.size)
        return BettingCheckpoint(
            integer("modality"), field("contestType"), integer("contest"), field("mode"), field("step"),
            field("numbers").integers(), field("clovers").integers(), field("team").optionalInt(), field("query"),
            field("month").optionalInt(), loteca, lotogol, field("superSeven").split(';').map { it.integers() },
            field("mirror").strictBoolean(), integer("quantity"), integer("recurring"), integer("surpriseQuantity"),
            integer("surpriseClovers"), integer("manualClovers"), field("favoriteName"), field("favoriteExpanded").strictBoolean()
        ).also { it.validate() }
    }

    private fun Int?.optional() = this?.toString() ?: "-"
    private fun String.optionalInt(): Int? = if (this == "-") null else toInt()
    private fun String.integers(): Set<Int> {
        if (isEmpty()) return emptySet()
        val items = split(',')
        require(items.size <= MAX_LIST_SIZE)
        return items.map(String::toInt).toSet().also { require(it.size == items.size) }
    }
    private fun String.strictBoolean() = when (this) {
        "true" -> true
        "false" -> false
        else -> throw IllegalArgumentException("Invalid checkpoint flag")
    }
}

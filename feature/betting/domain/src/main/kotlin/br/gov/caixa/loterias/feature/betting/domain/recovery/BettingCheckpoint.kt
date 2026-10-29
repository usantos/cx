package br.gov.caixa.loterias.feature.betting.domain.recovery

/** Edição parcial, deliberadamente independente dos objetos de envio e apresentação. */
data class BettingCheckpoint(
    val modalityId: Int,
    val contestType: String,
    val contestNumber: Int,
    val mode: String = "MANUAL",
    val step: String = "NUMBERS",
    val numbers: Set<Int> = emptySet(),
    val clovers: Set<Int> = emptySet(),
    val teamNumber: Int? = null,
    val teamQuery: String = "",
    val monthNumber: Int? = null,
    val loteca: Map<Int, Set<String>> = emptyMap(),
    val lotogol: Map<Int, Pair<Int?, Int?>> = emptyMap(),
    val superSeven: List<Set<Int>> = List(SUPER_SEVEN_COLUMNS) { emptySet() },
    val mirror: Boolean = false,
    val numberQuantity: Int = 0,
    val recurringDraws: Int = 0,
    val surpriseQuantity: Int = 1,
    val surpriseCloverQuantity: Int = 0,
    val manualCloverQuantity: Int = 0,
    val favoriteName: String = "",
    val favoriteExpanded: Boolean = false,
) {
    /** Somente identificadores exatos do simulador são representáveis; nunca um sucesso ou uma rota arbitrária. */
    val safeRoute: String get() {
        validate()
        return "betting/simulation/$modalityId?contestTypeId=${if (contestType == "NORMAL") 1 else 2}&contestNumber=$contestNumber"
    }

    fun validate() {
        validateIdentifiers()
        validateSelections()
        validateSports()
        validateQuantities()
    }

    private fun validateIdentifiers() {
        require(modalityId in 1..MAX_IDENTIFIER && contestNumber in 1..MAX_CONTEST)
        require(contestType in setOf("NORMAL", "ESPECIAL"))
        require(mode in setOf("MANUAL", "SURPRISE") && step in setOf("NUMBERS", "COMPLEMENT"))
        require(teamNumber == null || teamNumber in 1..MAX_TEAM)
        require(monthNumber == null || monthNumber in 1..MONTH_COUNT)
    }

    private fun validateSelections() {
        require(numbers.size <= MAX_SELECTION && numbers.all { it in 1..MAX_IDENTIFIER })
        require(clovers.size <= MAX_SELECTION && clovers.all { it in 1..MAX_IDENTIFIER })
        require(teamQuery.length <= MAX_QUERY_LENGTH && favoriteName.length <= MAX_FAVORITE_LENGTH)
        require(
            superSeven.size == SUPER_SEVEN_COLUMNS && superSeven.all { column ->
                column.size <= DIGIT_COUNT && column.all { it in 0 until DIGIT_COUNT }
            }
        )
    }

    private fun validateSports() {
        require(
            loteca.size <= MAX_MATCHES && loteca.all { (match, outcomes) ->
                match in 1..MAX_MATCHES && outcomes.size <= OUTCOME_COUNT &&
                    outcomes.all { it in setOf("HOME", "DRAW", "AWAY") }
            }
        )
        require(
            lotogol.size <= MAX_MATCHES && lotogol.all { (match, score) ->
                match in 1..MAX_MATCHES && listOfNotNull(score.first, score.second).all { it in 0..MAX_SCORE }
            }
        )
    }

    private fun validateQuantities() {
        require(numberQuantity in 0..MAX_QUANTITY && recurringDraws in 0..MAX_QUANTITY)
        require(
            surpriseQuantity in 1..MAX_QUANTITY && surpriseCloverQuantity in 0..MAX_SELECTION &&
                manualCloverQuantity in 0..MAX_SELECTION
        )
    }

    private companion object {
        const val SUPER_SEVEN_COLUMNS = 7
        const val MAX_IDENTIFIER = 1_000
        const val MAX_CONTEST = 10_000_000
        const val MAX_TEAM = 100_000
        const val MONTH_COUNT = 12
        const val MAX_SELECTION = 100
        const val MAX_QUERY_LENGTH = 256
        const val MAX_FAVORITE_LENGTH = 128
        const val DIGIT_COUNT = 10
        const val MAX_MATCHES = 32
        const val OUTCOME_COUNT = 3
        const val MAX_SCORE = 99
        const val MAX_QUANTITY = 1_000
    }
}

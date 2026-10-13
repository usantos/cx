package br.gov.caixa.loterias.core.domain.results

interface ResultsRepository {
    suspend fun latest(modality: String): DrawResult
    suspend fun byContest(modality: String, contest: Int): DrawResult
}

data class DrawResult(
    val modality: String,
    val contest: Int,
    val drawDate: String,
    val drawLocation: String,
    val firstDrawNumbers: List<Int>,
    val secondDrawNumbers: List<Int>,
    val clovers: List<Int>,
    val accumulated: Boolean,
    val accumulatedNextContest: String,
    val totalRevenue: String,
    val notice: String,
    val payouts: List<DrawPayout>,
)

data class DrawPayout(
    val description: String,
    val winners: Long,
    val value: String,
)

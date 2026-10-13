package br.gov.caixa.loterias.core.domain.marketplace

import java.util.Collections
import java.util.Objects

/** Immutable filter shared by presentation and data; every copy is normalized. */
class MarketplaceFilter @JvmOverloads constructor(
    minimumValue: String?,
    maximumValue: String?,
    minimumShares: Int?,
    maximumShares: Int?,
    betQuantity: Int?,
    numberQuantity: Int?,
    lottery: String?,
    modality: String?,
    wantedNumbers: List<Int>?,
    unwantedNumbers: List<Int>?,
    modalityId: Int? = null,
    contestType: Int? = null,
) {
    val minimumValue = minimumValue.orEmpty().trim()
    val maximumValue = maximumValue.orEmpty().trim()
    val minimumShares = minimumShares.positiveOrNull()
    val maximumShares = maximumShares.positiveOrNull()
    val betQuantity = betQuantity.positiveOrNull()
    val numberQuantity = numberQuantity.positiveOrNull()
    val lottery = lottery.orEmpty().trim()
    val modality = modality.orEmpty().trim()
    val modalityId = modalityId.positiveOrNull()
    val contestType = contestType.positiveOrNull()
    val wantedNumbers: List<Int> = immutableCopy(wantedNumbers)
    val unwantedNumbers: List<Int> = immutableCopy(unwantedNumbers)

    fun withBetQuantity(value: Int?) = copy(betQuantity = value)
    fun withNumberQuantity(value: Int?) = copy(numberQuantity = value)
    fun withWantedNumbers(values: List<Int>?) = copy(wantedNumbers = values)
    fun withUnwantedNumbers(values: List<Int>?) = copy(unwantedNumbers = values)
    fun withLottery(value: String?) = copy(lottery = value)
    fun withModality(id: Int?, type: Int?) = copy(modalityId = id, contestType = type)

    private fun copy(
        betQuantity: Int? = this.betQuantity,
        numberQuantity: Int? = this.numberQuantity,
        wantedNumbers: List<Int>? = this.wantedNumbers,
        unwantedNumbers: List<Int>? = this.unwantedNumbers,
        lottery: String? = this.lottery,
        modalityId: Int? = this.modalityId,
        contestType: Int? = this.contestType,
    ) = MarketplaceFilter(
        minimumValue, maximumValue, minimumShares, maximumShares, betQuantity,
        numberQuantity, lottery, modality, wantedNumbers, unwantedNumbers, modalityId, contestType,
    )

    override fun equals(other: Any?): Boolean = this === other || other is MarketplaceFilter &&
        minimumValue == other.minimumValue && maximumValue == other.maximumValue &&
        minimumShares == other.minimumShares && maximumShares == other.maximumShares &&
        betQuantity == other.betQuantity && numberQuantity == other.numberQuantity &&
        lottery == other.lottery && modality == other.modality &&
        modalityId == other.modalityId && contestType == other.contestType &&
        wantedNumbers == other.wantedNumbers && unwantedNumbers == other.unwantedNumbers

    override fun hashCode(): Int = Objects.hash(
        minimumValue, maximumValue, minimumShares, maximumShares, betQuantity,
        numberQuantity, lottery, modality, modalityId, contestType, wantedNumbers, unwantedNumbers,
    )

    companion object {
        @JvmStatic
        fun empty() = MarketplaceFilter("", "", null, null, null, null, "", "", null, null)

        @JvmStatic
        fun forModality(modalityId: Int, contestType: Int) = empty().withModality(modalityId, contestType)

        private fun Int?.positiveOrNull() = this?.takeIf { it > 0 }
        private fun immutableCopy(values: List<Int>?): List<Int> =
            Collections.unmodifiableList(ArrayList(values.orEmpty()))
    }
}

package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.retail.Retailer
import br.gov.caixa.loterias.core.domain.tickets.*
import java.io.*
import java.math.BigDecimal

/** Navigation context only: authentication is restored by its own session provider. */
internal enum class DetailRestorationOutcome { None, Pending, Restored, Discarded }

internal class LoggedDetailContext(owner: String?) {
    var owner: String? = owner
        private set
    var restorationOutcome by mutableStateOf(DetailRestorationOutcome.None)
        private set
    internal var pendingSnapshot: ByteArray? = null
        private set
    var marketplaceFilter by mutableStateOf(MarketplaceFilter.empty())
    var marketplaceClearFilter by mutableStateOf(MarketplaceFilter.empty())
    var lastAppliedFilterRetailer by mutableStateOf<Retailer?>(null)
    var retailerMarketplaceFilter by mutableStateOf(MarketplaceFilter.empty())
    var selectedPurchase by mutableStateOf<Purchase?>(null)
    var selectedBet by mutableStateOf<ConfirmedBet?>(null)
    var betToEdit by mutableStateOf<ConfirmedBet?>(null)
    var selectedMarketplaceRetailer by mutableStateOf<Retailer?>(null)
    var checkedTicketBets by mutableStateOf<List<ConfirmedBet>>(emptyList())

    /** Logout removes both visible data and a still-locked process-restoration snapshot. */
    fun clear() {
        pendingSnapshot = null
        restorationOutcome = DetailRestorationOutcome.None
        owner = null
        clearFields()
    }

    private fun clearFields() {
        marketplaceFilter = MarketplaceFilter.empty()
        marketplaceClearFilter = MarketplaceFilter.empty()
        lastAppliedFilterRetailer = null
        retailerMarketplaceFilter = MarketplaceFilter.empty()
        selectedPurchase = null
        selectedBet = null
        betToEdit = null
        selectedMarketplaceRetailer = null
        checkedTicketBets = emptyList()
    }

    internal fun defer(bytes: ByteArray) {
        clear()
        pendingSnapshot = bytes.copyOf()
        restorationOutcome = DetailRestorationOutcome.Pending
    }

    /** A missing session never unlocks private fields; interactive reauthentication may unlock them. */
    fun reconcile(authenticatedOwner: String?) {
        val nextOwner = authenticatedOwner?.takeIf { it.isNotBlank() }
        if (nextOwner == owner) return
        if (nextOwner == null) {
            val snapshot = LoggedDetailContextCodec.save(this)
            clearFields()
            owner = null
            pendingSnapshot = snapshot
            restorationOutcome = if (snapshot == null) DetailRestorationOutcome.None else DetailRestorationOutcome.Pending
            return
        }
        val pending = pendingSnapshot
        if (pending != null) {
            val restored = LoggedDetailContextCodec.restore(pending, nextOwner)
            clearFields()
            pendingSnapshot = null
            if (restored != null) {
                marketplaceFilter = restored.marketplaceFilter
                marketplaceClearFilter = restored.marketplaceClearFilter
                lastAppliedFilterRetailer = restored.lastAppliedFilterRetailer
                retailerMarketplaceFilter = restored.retailerMarketplaceFilter
                selectedPurchase = restored.selectedPurchase
                selectedBet = restored.selectedBet
                betToEdit = restored.betToEdit
                selectedMarketplaceRetailer = restored.selectedMarketplaceRetailer
                checkedTicketBets = restored.checkedTicketBets
                restorationOutcome = DetailRestorationOutcome.Restored
            } else {
                restorationOutcome = DetailRestorationOutcome.Discarded
            }
        } else if (owner != null && owner != nextOwner) {
            clearFields()
            restorationOutcome = DetailRestorationOutcome.Discarded
        }
        owner = nextOwner
    }
}

@Composable
internal fun rememberLoggedDetailContext(owner: String?): LoggedDetailContext {
    val context = rememberSaveable(
        saver = Saver(
            save = { LoggedDetailContextCodec.save(it) },
            restore = { LoggedDetailContextCodec.defer(it) },
        ),
    ) { LoggedDetailContext(owner) }
    context.reconcile(owner)
    return context
}

/** A single size budget protects the activity Bundle; invalid/oversized contexts are discarded. */
internal object LoggedDetailContextCodec {
    private const val VERSION = 2
    private const val MAX_BYTES = 96 * 1024
    private const val MAX_ITEMS = 512

    fun defer(bytes: ByteArray): LoggedDetailContext? = runCatching {
        if (bytes.size > MAX_BYTES) return null
        DataInputStream(ByteArrayInputStream(bytes)).use { input ->
            require(input.readInt() in 1..VERSION)
            require(input.readUTF().isNotBlank())
        }
        LoggedDetailContext(null).apply { defer(bytes) }
    }.getOrNull()

    fun save(context: LoggedDetailContext): ByteArray? = runCatching {
        context.pendingSnapshot?.let { return it.copyOf() }
        val owner = context.owner?.takeIf { it.isNotBlank() } ?: return null
        val bytes = ByteArrayOutputStream()
        val bounded = object : OutputStream() {
            override fun write(value: Int) { require(bytes.size() < MAX_BYTES); bytes.write(value) }
            override fun write(values: ByteArray, offset: Int, length: Int) {
                require(length <= MAX_BYTES - bytes.size()); bytes.write(values, offset, length)
            }
        }
        DataOutputStream(bounded).use { out ->
            out.writeInt(VERSION); out.writeUTF(owner)
            out.optional(context.selectedPurchase) { purchase(it) }
            out.optional(context.selectedBet) { bet(it) }
            out.optional(context.betToEdit) { bet(it) }
            out.filter(context.marketplaceFilter)
            out.optional(context.selectedMarketplaceRetailer) { retailer(it) }
            out.items(context.checkedTicketBets) { bet(it) }
            out.filter(context.marketplaceClearFilter)
            out.optional(context.lastAppliedFilterRetailer) { retailer(it) }
            out.filter(context.retailerMarketplaceFilter)
        }
        bytes.toByteArray()
    }.getOrNull()

    fun restore(bytes: ByteArray, owner: String?): LoggedDetailContext? = runCatching {
        if (owner.isNullOrBlank() || bytes.size > MAX_BYTES) return null
        DataInputStream(ByteArrayInputStream(bytes)).use { input ->
            val version = input.readInt()
            if (version !in 1..VERSION || input.readUTF() != owner) return null
            LoggedDetailContext(owner).apply {
                selectedPurchase = input.optional { purchase() }
                selectedBet = input.optional { bet() }
                betToEdit = input.optional { bet() }
                marketplaceFilter = input.filter()
                selectedMarketplaceRetailer = input.optional { retailer() }
                checkedTicketBets = input.items { bet() }
                if (version >= 2) {
                    marketplaceClearFilter = input.filter()
                    lastAppliedFilterRetailer = input.optional { retailer() }
                    retailerMarketplaceFilter = input.filter()
                } else {
                    marketplaceClearFilter = MarketplaceFilter.forModality(marketplaceFilter.modalityId ?: 0, marketplaceFilter.contestType ?: 0)
                }
                require(input.available() == 0)
            }
        }
    }.getOrNull()

    private fun <T> DataOutputStream.optional(value: T?, write: DataOutputStream.(T) -> Unit) {
        writeBoolean(value != null); if (value != null) write(value)
    }
    private fun <T> DataInputStream.optional(decode: DataInputStream.() -> T): T? = if (readBoolean()) decode() else null
    private fun <T> DataOutputStream.items(values: List<T>, write: DataOutputStream.(T) -> Unit) {
        require(values.size <= MAX_ITEMS); writeInt(values.size); values.forEach { write(it) }
    }
    private fun <T> DataInputStream.items(decode: DataInputStream.() -> T): List<T> {
        val count = readInt(); require(count in 0..MAX_ITEMS); return List(count) { decode() }
    }
    private fun DataOutputStream.strings(vararg values: String) = values.forEach { writeUTF(it) }
    private fun DataOutputStream.ints(values: List<Int>) = items(values) { writeInt(it) }
    private fun DataInputStream.ints() = items { readInt() }
    private fun DataOutputStream.purchase(v: Purchase) {
        writeLong(v.id); strings(v.date, v.status, v.paymentMethod, v.total.toPlainString(), v.pixCopyPaste, v.paymentExpiresAt)
        writeLong(v.statusId); writeLong(v.paymentMethodId); strings(v.finalizedTime, v.endToEndId)
    }
    private fun DataInputStream.purchase() = Purchase(readLong(), readUTF(), readUTF(), readUTF(), BigDecimal(readUTF()),
        readUTF(), readUTF(), readLong(), readLong(), readUTF(), readUTF())
    private fun DataOutputStream.retailer(v: Retailer) {
        writeLong(v.id); strings(v.code, v.fantasyName, v.legalName, v.address, v.district, v.city, v.state); writeBoolean(v.favorite)
    }
    private fun DataInputStream.retailer() = Retailer(readLong(), readUTF(), readUTF(), readUTF(), readUTF(), readUTF(), readUTF(), readUTF(), readBoolean())
    private fun DataOutputStream.filter(v: MarketplaceFilter) {
        strings(v.minimumValue, v.maximumValue)
        listOf(v.minimumShares, v.maximumShares, v.betQuantity, v.numberQuantity).forEach { optional(it) { writeInt(it) } }
        strings(v.lottery, v.modality); ints(v.wantedNumbers); ints(v.unwantedNumbers)
        optional(v.modalityId) { writeInt(it) }; optional(v.contestType) { writeInt(it) }
    }
    private fun DataInputStream.filter() = MarketplaceFilter(readUTF(), readUTF(), optional { readInt() }, optional { readInt() },
        optional { readInt() }, optional { readInt() }, readUTF(), readUTF(), ints(), ints(), optional { readInt() }, optional { readInt() })
    private fun DataOutputStream.team(v: SportsTeam) {
        writeInt(v.number); strings(v.name, v.state, v.shortDescription, v.longDescription); writeInt(v.countryNumber)
        strings(v.country, v.countryCode, v.className); writeBoolean(v.selectionIndicator); optional(v.shieldBase64) { writeUTF(it) }
    }
    private fun DataInputStream.team() = SportsTeam(readInt(), readUTF(), readUTF(), readUTF(), readUTF(), readInt(), readUTF(), readUTF(), readUTF(), readBoolean(), optional { readUTF() })
    private fun DataOutputStream.match(v: SportsMatch) { writeInt(v.number); team(v.home); team(v.away); strings(v.legendDescription, v.legendLetter) }
    private fun DataInputStream.match() = SportsMatch(readInt(), team(), team(), readUTF(), readUTF())
    private fun DataOutputStream.bet(v: ConfirmedBet) {
        writeLong(v.id); writeUTF(v.modality); writeInt(v.contest); strings(v.date, v.status); ints(v.numbers)
        writeUTF(v.prize.toPlainString()); writeBoolean(v.originalRecurringBet); writeBoolean(v.redeemedPrize)
        optional(v.exchangeBetId) { writeLong(it) }; writeInt(v.modalityId); writeLong(v.statusId); writeLong(v.contestTypeId)
        writeBoolean(v.poolShare); writeBoolean(v.surprise); writeUTF(v.time); items(v.numberColumns) { ints(it) }; ints(v.clovers)
        optional(v.favoriteTeam) { team(it) }
        optional(v.luckyMonth) { writeInt(it.number); strings(it.name, it.abbreviation) }
        items(v.lotecaSelections) { match(it.match); writeBoolean(it.homeWin); writeBoolean(it.draw); writeBoolean(it.awayWin) }
        items(v.lotogolSelections) { match(it.match); writeInt(it.homeScore); writeInt(it.awayScore) }
        writeInt(v.recurringDraws); writeInt(v.quantity); writeBoolean(v.mirror); writeBoolean(v.exchange); writeBoolean(v.combo)
    }
    private fun DataInputStream.bet() = ConfirmedBet(readLong(), readUTF(), readInt(), readUTF(), readUTF(), ints(),
        BigDecimal(readUTF()), readBoolean(), readBoolean(), optional { readLong() }, readInt(), readLong(), readLong(),
        readBoolean(), readBoolean(), readUTF(), items { ints() }, ints(), optional { team() },
        optional { LuckyMonth(readInt(), readUTF(), readUTF()) },
        items { LotecaMatchSelection(match(), readBoolean(), readBoolean(), readBoolean()) },
        items { LotogolMatchSelection(match(), readInt(), readInt()) }, readInt(), readInt(), readBoolean(), readBoolean(), readBoolean())
}

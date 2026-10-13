package br.gov.caixa.loterias.core.preferences

import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.*
import java.io.File
import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class VisitorCartStorageTest {
    private lateinit var context: Context
    private lateinit var directory: File
    private val game = LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 3000, "", 60, 6, 20, 10, emptyList(), listOf(BetPrice(6, 0, BigDecimal("6.00"))))
    private val parameters = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 10, listOf(game))
    private val draft = BetDraft(2, numbers = listOf(1, 2, 3, 4, 5, 6))
    @Before fun setUp() {
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        directory = File(target.cacheDir, "visitor-test-${UUID.randomUUID()}")
        check(directory.mkdirs())
        context = object : ContextWrapper(target) {
            override fun getApplicationContext(): Context = this
            override fun getDatabasePath(name: String) = File(directory, name)
        }
    }
    @After fun tearDown() { check(directory.deleteRecursively()) }
    @Test fun cartSurvivesRecreationAndRemovalWithoutChangingUnrelatedTables() = runBlocking {
        SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("Loterias.db"), null).use {
            it.version = 49
            it.execSQL("CREATE TABLE unrelated (value TEXT)")
            it.execSQL("INSERT INTO unrelated VALUES ('preserve')")
        }
        val local = SQLiteVisitorCartRepository(context)
        val cart = local.add(draft.copy(recurringDraws = 3, superSevenColumns = List(7) { emptyList() }), parameters)
        assertEquals(BigDecimal("18.00"), cart.total)
        val recreated = SQLiteVisitorCartRepository(context)
        assertEquals(cart, recreated.current())
        assertEquals(1, LegacyLocalCartReader.fromContext(context).snapshot().size)
        assertTrue(recreated.remove(cart.items.single()).items.isEmpty())
        SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("Loterias.db"), null).use {
            assertEquals(49, it.version)
            it.rawQuery("SELECT value FROM unrelated", null).use { c -> assertTrue(c.moveToFirst()); assertEquals("preserve", c.getString(0)) }
        }
    }
    @Test fun mirroredPairIsAtomicAndRemovalRemovesBoth() = runBlocking {
        val lotomania = game.copy(modality = LotteryModality(16, "Lotomania", "LOTOMANIA"), maximumNumber = 100, minimumSelection = 50, maximumSelection = 50, prices = listOf(BetPrice(50, 0, BigDecimal("3.00"))), acceptsMirror = true)
        val local = SQLiteVisitorCartRepository(context)
        val cart = local.add(BetDraft(16, numbers = (1..50).toList(), generateMirror = true), parameters.copy(games = listOf(lotomania)))
        assertEquals(2, cart.betCount)
        assertEquals(BigDecimal("6.00"), cart.total)
        val bets = LegacyLocalCartReader.fromContext(context).snapshot()
        assertEquals(bets.first().mirrorLink, bets.last().mirrorLink)
        assertTrue(bets.last().mirrored)
        assertEquals((51..100).toList(), (bets.last().numbers as LegacyCartNumbers.Flat).values)
        assertTrue(local.remove(cart.items.first()).items.isEmpty())
    }
    @Test fun surpriseMirrorsKeepTwoOpaqueRowsPerBetAndTotalAcrossRecreation() = runBlocking {
        val lotomania = game.copy(modality = LotteryModality(16, "Lotomania", "LOTOMANIA"), maximumNumber = 100, minimumSelection = 50, maximumSelection = 50, prices = listOf(BetPrice(50, 0, BigDecimal("3.00"))), acceptsMirror = true)
        val local = SQLiteVisitorCartRepository(context)
        val cart = local.add(BetDraft(16, numbers = emptyList(), surprise = true, quantity = 2, numberQuantity = 50, generateMirror = true), parameters.copy(games = listOf(lotomania)))
        assertEquals(4, cart.betCount)
        assertEquals(BigDecimal("12.00"), cart.total)
        assertEquals(2, LegacyLocalCartReader.fromContext(context).snapshot().count { it.mirrored })
        assertTrue(cart.items.all { it.selectedNumbers.flatten().isEmpty() })
        assertEquals(cart, SQLiteVisitorCartRepository(context).current())
    }

    @Test fun quantityLimitRejectsWholeBatchAndConfirmedTransferDeletesOnlyAcknowledgedRows() = runBlocking {
        val local = SQLiteVisitorCartRepository(context)
        local.add(draft, parameters)
        assertTrue(runCatching { local.add(draft.copy(quantity = 10), parameters) }.isFailure)
        assertEquals(1, local.current().betCount)
        val transfer = LegacyCartTransferLocalRepository.fromContext(context)
        val owner = "a".repeat(64)
        val ready = transfer.prepare(owner) as LegacyCartTransferPreparation.Ready
        local.add(draft.copy(numbers = listOf(7, 8, 9, 10, 11, 12)), parameters)
        assertTrue(transfer.confirmReceived(owner, ready.transferId))
        assertEquals(LegacyCartCleanupResult.Complete, transfer.finishConfirmed(owner, ready.transferId))
        assertEquals(listOf(7, 8, 9, 10, 11, 12), local.current().items.single().selectedNumbers.single())
    }
    @Test fun replacementAfterClearCannotReuseReceiptIdOrBeDeletedByOldConfirmation() = runBlocking {
        val local = SQLiteVisitorCartRepository(context)
        val original = local.add(draft, parameters).items.single()
        val owner = "a".repeat(64)
        val transfer = LegacyCartTransferLocalRepository.fromContext(context)
        val ready = transfer.prepare(owner) as LegacyCartTransferPreparation.Ready
        // Simulate a receipt predating the new persistent allocator.
        SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("Loterias.db"), null).use {
            it.execSQL("DROP TABLE compose_visitor_cart_sequence_v1")
        }
        local.clear()
        val replacement = local.add(draft, parameters).items.single()
        assertTrue(replacement.id > original.id)
        assertTrue(transfer.confirmReceived(owner, ready.transferId))
        assertEquals(LegacyCartCleanupResult.Complete, transfer.finishConfirmed(owner, ready.transferId))
        assertEquals(replacement, local.current().items.single())
    }

    @Test fun sportsTeamsAndColumnsRemainReadableByTheTransferProtocol() = runBlocking {
        val team = SportsTeam(10, "Time", countryNumber = 55, selectionIndicator = true)
        val match = SportsMatch(1, team, team.copy(number = 11, name = "Outro"))
        val loteca = game.copy(modality = LotteryModality(19, "Loteca", "LOTECA"), matches = listOf(match), prices = listOf(BetPrice(0, 0, BigDecimal("4.00"), doubleQuantity = 1)))
        val local = SQLiteVisitorCartRepository(context)
        local.add(BetDraft(19, numbers = emptyList(), lotecaSelections = listOf(LotecaMatchSelection(match, true, true, false))), parameters.copy(games = listOf(loteca)))
        val bet = LegacyLocalCartReader.fromContext(context).snapshot().single()
        assertTrue(bet.loteca.single().draw)
        assertEquals("Time", bet.loteca.single().first.team.name)
        assertTrue(bet.loteca.single().first.winner)
        local.clear()
        val seven = game.copy(modality = LotteryModality(7, "Super Sete", "SUPER_7"), minimumSelection = 7, maximumSelection = 21, prices = listOf(BetPrice(7, 0, BigDecimal("3.00"))))
        local.add(BetDraft(7, numbers = emptyList(), superSevenColumns = List(7) { listOf(it) }, numberQuantity = 7), parameters.copy(games = listOf(seven)))
        assertEquals(LegacyCartNumbers.Columns(List(7) { listOf(it) }), LegacyLocalCartReader.fromContext(context).snapshot().single().numbers)
    }

    @Test fun duplicateRequiresExplicitConfirmationAndStaleContestIsRejected() = runBlocking {
        val local = SQLiteVisitorCartRepository(context)
        local.add(draft, parameters)
        assertEquals(VISITOR_DUPLICATE_CONFIRMATION, runCatching { local.add(draft, parameters) }.exceptionOrNull()?.message)
        assertEquals(1, local.current().betCount)
        assertEquals(2, local.add(draft.copy(allowLocalDuplicate = true), parameters).betCount)
        assertTrue(runCatching { local.add(draft.copy(contestNumber = 2999), parameters) }.isFailure)
        assertEquals(2, local.current().betCount)
    }

    @Test fun corruptDatabaseIsPreserved() = runBlocking {
        val file = context.getDatabasePath("Loterias.db")
        val bytes = ByteArray(4096) { 65 }
        file.writeBytes(bytes)
        assertTrue(runCatching { SQLiteVisitorCartRepository(context).add(draft, parameters) }.isFailure)
        assertArrayEquals(bytes, file.readBytes())
    }
}

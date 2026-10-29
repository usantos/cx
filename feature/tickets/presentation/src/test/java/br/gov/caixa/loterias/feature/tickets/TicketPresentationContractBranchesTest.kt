// Literais numéricos descrevem seleções de loteria, identificadores e dados de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.tickets

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_INVALID_IDENTIFICATION
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_OPERATION_FAILED
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_REQUEST_FAILED
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNotice
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNoticeReason
import br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketAward
import br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketResult
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultAwardRow
import br.gov.caixa.loterias.feature.tickets.presentation.components.ticketResultVisualData
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.ticketsPixNoticeUi
import br.gov.caixa.loterias.feature.tickets.presentation.route.betReceiptViewIntent
import br.gov.caixa.loterias.feature.tickets.presentation.route.openBetReceipt
import br.gov.caixa.loterias.feature.tickets.presentation.route.writeBetReceipt
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetReceipt
import java.io.File
import java.math.BigDecimal
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TicketPresentationContractBranchesTest {
    private val now = LocalDateTime.of(2026, 10, 22, 13, 14, 15)

    @Test fun resultAwardsFormatNumbersAndPreserveMissingFieldsWithoutInventingValues() {
        val visual = ticketResultVisualData(
            CheckedTicketResult(
                statusId = 100,
                modalityId = 2,
                modalityName = "Mega-Sena",
                netPrize = BigDecimal.TEN,
                awards = listOf(
                    CheckedTicketAward(123, "Premiada", "Quadra", 4, 1, BigDecimal("25.50")),
                    CheckedTicketAward(null, "Pendente")
                )
            )
        )
        assertEquals(100, visual.ticketStatusId)
        assertEquals(2, visual.modalityId)
        assertEquals("Mega-Sena", visual.modalityName)
        assertEquals(BigDecimal.TEN, visual.netPrize)
        assertEquals(
            listOf("123", "Premiada", "Quadra", "4", "1"),
            visual.awardRows.first().let {
                listOf(it.contest, it.status, it.award, it.quantity, it.draw)
            }
        )
        assertTrue(visual.awardRows.first().value.endsWith("25,50"))
        assertEquals(TicketResultAwardRow("-", "Pendente", "-", "-", "-", "-"), visual.awardRows.last())
    }

    @Test fun nextContestMapsEstimateAndNativeClosingDateButAbsentNextContestHidesBoth() {
        val game =
            LotteryGameParameters(
                LotteryModality(
                    2,
                    "Mega-Sena",
                    "MEGA_SENA"
                ),
                123, "22/10/2026", 60, 6, 20, 10, emptyList(), emptyList(),
                estimatedPrize = BigDecimal(
                    "1000000"
                ),
                closingDateTime = "23/10/2026 19:30:00", hasNextContest = true
            )
        val visual = ticketResultVisualData(CheckedTicketResult(), game)
        assertTrue(visual.currentEstimate!!.endsWith("1.000.000,00"))
        assertEquals("23/10 | 19h30", visual.currentClosing)
        assertEquals(
            "Em definição",
            ticketResultVisualData(CheckedTicketResult(), game.copy(closingDateTime = "Em definição")).currentClosing
        )
        assertNull(ticketResultVisualData(CheckedTicketResult(), game.copy(closingDateTime = " ")).currentClosing)
        val hidden = ticketResultVisualData(CheckedTicketResult(), game.copy(hasNextContest = false))
        assertNull(hidden.currentEstimate)
        assertNull(hidden.currentClosing)
    }

    @Test fun pixBusinessActionsOfferOnlyTheirOwnContinuationWithoutDiagnostics() {
        val more = ticketsPixNoticeUi(PurchaseNotice(PurchaseNoticeReason.MORE_BETS_REQUIRED, "Adicione apostas", "A1"), now)
        assertEquals("Adicione apostas", more.message)
        assertTrue(more.offerMoreBets)
        assertFalse(more.offerRegistration)
        val registration = ticketsPixNoticeUi(PurchaseNotice(PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION), now)
        assertEquals(LEGACY_OPERATION_FAILED, registration.message)
        assertTrue(registration.offerRegistration)
        assertFalse(registration.offerMoreBets)
        val invalid = ticketsPixNoticeUi(PurchaseNotice(PurchaseNoticeReason.INVALID_IDENTIFICATION, "Ignorado", "X"), now)
        assertEquals(LEGACY_INVALID_IDENTIFICATION, invalid.message)
        assertFalse(invalid.offerMoreBets)
        assertFalse(invalid.offerRegistration)
    }

    @Test fun pixFailuresKeepBackendMessageAndResponseTimeDiagnostics() {
        val server = ticketsPixNoticeUi(PurchaseNotice(PurchaseNoticeReason.SERVER_MESSAGE, "Servidor respondeu", "S42"), now)
        assertEquals("Servidor respondeu\n\nS42\n22/10/2026 13:14:15", server.message)
        val fallback = ticketsPixNoticeUi(PurchaseNotice(PurchaseNoticeReason.SERVER_FAILURE, "Ignorado"), now)
        assertEquals("$LEGACY_REQUEST_FAILED\n\n22/10/2026 13:14:15", fallback.message)
        val empty = ticketsPixNoticeUi(PurchaseNotice(PurchaseNoticeReason.BUSINESS_RULE, ""), now)
        assertEquals("$LEGACY_OPERATION_FAILED\n\n22/10/2026 13:14:15", empty.message)
        assertFalse(server.offerMoreBets)
        assertFalse(server.offerRegistration)
    }

    @Test fun receiptIntentGrantsReadAccessAndOpenReportsMissingPdfViewer() {
        val uri = "content://test.receipts/receipt.pdf".toUri()
        val intent = betReceiptViewIntent(uri)
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("application/pdf", intent.type)
        assertEquals(uri, intent.data)
        assertEquals(uri, intent.clipData!!.getItemAt(0).uri)
        assertTrue(intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0)
        assertTrue(intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
        val base = ApplicationProvider.getApplicationContext<Context>()
        var opened: Intent? = null
        val viewer = object : ContextWrapper(base) {
            override fun startActivity(intent: Intent) {
                opened = intent
            }
        }
        assertTrue(openBetReceipt(viewer, uri))
        assertEquals(uri, opened?.data)
        val missing = object : ContextWrapper(base) {
            override fun startActivity(intent: Intent): Unit = throw android.content.ActivityNotFoundException("Sem leitor PDF")
        }
        assertFalse(openBetReceipt(missing, uri))
    }

    @Test fun invalidReceiptHeaderIsRejectedBeforeWritingToCache() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "invalid-contract-receipt.pdf"
        val file = File(context.cacheDir, "receipts/$name")
        file.delete()
        assertThrows(IllegalArgumentException::class.java) { writeBetReceipt(context, BetReceipt("not a pdf".toByteArray(), name)) }
        assertFalse(file.exists())
    }
}

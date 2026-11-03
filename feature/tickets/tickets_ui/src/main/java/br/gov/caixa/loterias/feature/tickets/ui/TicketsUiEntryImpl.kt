package br.gov.caixa.loterias.feature.tickets.ui

import androidx.annotation.Keep
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsBetDetailScreenEntry
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsConfirmedBetsContentEntry
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsFilterDialogsEntry
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsManualTicketContentEntry
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsPurchaseDetailContentEntry
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsPurchaseDetailDialogsEntry
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsPurchasesContentEntry
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsTicketGameChoiceScreenEntry
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsTicketResultScreenEntry
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsTicketScannerScreenEntry
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry

@Keep
class TicketsUiEntryImpl :
    TicketsUiEntry,
    TicketsFilterDialogsEntry by TicketsFilterDialogsRenderer(),

    TicketsTicketResultScreenEntry by TicketsTicketResultScreenRenderer(),
    TicketsTicketScannerScreenEntry by TicketsTicketScannerScreenRenderer(),

    TicketsTicketGameChoiceScreenEntry by TicketsTicketGameChoiceScreenRenderer(),
    TicketsConfirmedBetsContentEntry by TicketsConfirmedBetsContentRenderer(),
    TicketsPurchaseDetailDialogsEntry by TicketsPurchaseDetailDialogsRenderer(),

    TicketsPurchaseDetailContentEntry by TicketsPurchaseDetailContentRenderer(),

    TicketsPurchasesContentEntry by TicketsPurchasesContentRenderer(),

    TicketsBetDetailScreenEntry by TicketsBetDetailScreenRenderer(),

    TicketsManualTicketContentEntry by TicketsManualTicketContentRenderer()

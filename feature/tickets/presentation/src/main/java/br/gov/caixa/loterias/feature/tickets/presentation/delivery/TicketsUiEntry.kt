package br.gov.caixa.loterias.feature.tickets.presentation.delivery

import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry

interface TicketsUiEntry :
    FeatureUiEntry,
    TicketsFilterDialogsEntry,
    TicketsPurchasesContentEntry,
    TicketsTicketResultScreenEntry,
    TicketsPurchaseDetailDialogsEntry,
    TicketsTicketGameChoiceScreenEntry,
    TicketsConfirmedBetsContentEntry,
    TicketsPurchaseDetailContentEntry,
    TicketsBetDetailScreenEntry,
    TicketsTicketScannerScreenEntry,
    TicketsManualTicketContentEntry

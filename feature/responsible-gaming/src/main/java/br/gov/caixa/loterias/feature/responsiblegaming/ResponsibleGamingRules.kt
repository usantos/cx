package br.gov.caixa.loterias.feature.responsiblegaming

internal fun canAdvanceAssessment(answer: String?): Boolean = answer == "Sim" || answer == "Não"

internal fun canSubmitSuspension(period: String?, confirmed: Boolean): Boolean =
    confirmed && period in setOf("24 horas", "48 horas", "7 dias", "15 dias", "30 dias")

package br.gov.caixa.loterias.feature.responsiblegaming.domain.repository

/** Cria um vínculo opaco com a sessão atual antes de agendar a operação. */
fun interface SuspensionRepositoryFactory {
    fun forCurrentSession(): ResponsibleGamingRepository
}

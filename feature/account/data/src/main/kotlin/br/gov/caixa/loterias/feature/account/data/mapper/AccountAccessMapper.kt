package br.gov.caixa.loterias.feature.account.data.mapper

import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AccountStatus
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.data.dto.AccountSessionDto
import java.math.BigDecimal

/** Compartilhado pelas atualizações comuns de conta e pelo endpoint pós-login vinculado à sessão. */
internal fun accountAccess(session: AccountSessionDto?, redirect: String?): AccountAccess {
    val destination = when (redirect?.trim()?.lowercase()) {
        "cadastrar-apostador", "ativar-cadastro" -> PostLoginDestination.REGISTRATION
        "aceitar-termo-de-uso" -> PostLoginDestination.TERMS
        null -> if (checkNotNull(session) { "Cadastro não retornado pelo serviço" }.requiresSelfAssessment == true) {
            PostLoginDestination.SELF_ASSESSMENT
        } else {
            PostLoginDestination.HOME
        }
        else -> error("Destino de cadastro não reconhecido")
    }
    return AccountAccess(
        destination = destination,
        temporarySuspension = session?.temporarySuspension == true,
        dailyLimit = session?.dailyLimit ?: BigDecimal.ZERO,
        status = when (session?.status?.kind) {
            null -> null
            "ATIVO" -> AccountStatus.ACTIVE
            "BLOQUEADO" -> AccountStatus.BLOCKED
            "BLOQUEADO_TOTAL" -> AccountStatus.TOTALLY_BLOCKED
            "BLOQUEADO_PARCIAL" -> AccountStatus.PARTIALLY_BLOCKED
            else -> AccountStatus.UNKNOWN
        },
    )
}

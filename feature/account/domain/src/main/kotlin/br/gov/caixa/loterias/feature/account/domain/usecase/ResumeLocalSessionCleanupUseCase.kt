package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.feature.account.domain.model.LocalSessionRecovery

fun interface ResumeLocalSessionCleanupUseCase { suspend operator fun invoke(): LocalSessionRecovery }

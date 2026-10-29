package br.gov.caixa.loterias.feature.onboarding.domain.usecase

fun interface ShouldShowIntroductionUseCase {
    suspend operator fun invoke(): Boolean
}

package br.gov.caixa.loterias.feature.betting.domain.usecase

fun interface GetShakeEnabledUseCase {
    suspend operator fun invoke(): Boolean
}

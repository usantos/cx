package br.gov.caixa.loterias.feature.betting.presentation.state
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import java.math.BigDecimal
internal sealed interface RapidConfigurationAction {
    data object AmountLimitsLoading : RapidConfigurationAction
    data class AmountLimitsLoaded(val minimum: BigDecimal, val maximum: BigDecimal) : RapidConfigurationAction
    data class AmountLimitsFailed(val message: String = messageText("nao_foi_possivel_carregar_os_lim_08d889")) : RapidConfigurationAction
    data class MinimumPrizeChanged(val value: BigDecimal) : RapidConfigurationAction
    data class MinimumAmountChanged(val value: BigDecimal) : RapidConfigurationAction
    data class MaximumAmountChanged(val value: BigDecimal) : RapidConfigurationAction
    data class MinimumIntervalCorrected(
        val floor: BigDecimal,
        val maximum: BigDecimal,
        val message: String =
            messageText(
                "o_valor_escolhido_tem_que_ser_um_01a660",
                br.gov.caixa.loterias.feature.betting.presentation.mapper.formatRapidAmount(maximum),
                br.gov.caixa.loterias.feature.betting.presentation.mapper.formatRapidAmount(floor)
            ),
    ) : RapidConfigurationAction
    data object FavoritesLoading : RapidConfigurationAction
    data class FavoritesLoaded(val favorites: List<FavoriteBet>) : RapidConfigurationAction
    data class FavoritesFailed(val error: AppError, val message: String = captureRapidFavoritesFailure(error)) : RapidConfigurationAction
    data class FavoriteToggled(val id: Long) : RapidConfigurationAction
    data object ConfigurationLoading : RapidConfigurationAction
    data class ConfigurationLoaded(val configuration: RapidBetConfiguration, val available: List<LotteryModality>, val selected: Set<Int>) :
        RapidConfigurationAction
    data class Failed(val error: AppError, val message: String = captureRapidConfigurationFailure(error)) : RapidConfigurationAction
    data object RequiredNumbersCleared : RapidConfigurationAction
    data object ExcludedNumbersCleared : RapidConfigurationAction
    data class NumberToggled(
        val number: Int,
        val required: Boolean,
        val message: String = messageText("um_numero_nao_pode_ser_marcado_c_e7cc40"),
    ) : RapidConfigurationAction
    data class InvalidConfiguration(val message: String = messageText("revise_a_configuracao_do_rapidao_9fabb5")) : RapidConfigurationAction
    data object SavingStarted : RapidConfigurationAction
    data object ConfigurationSaved : RapidConfigurationAction
    data class PageChanged(val page: Int) : RapidConfigurationAction
    data class ModalityToggled(val id: Int) : RapidConfigurationAction
    data object MessageDismissed : RapidConfigurationAction
    data class ServiceFailureReceived(val presentation: ServiceFailurePresentation) : RapidConfigurationAction
    data class ServiceNoticeDismissed(val expected: ServiceNoticeUiState) : RapidConfigurationAction
    data class ServiceNoticeAccepted(val expected: ServiceNoticeUiState, val presentation: ServiceFailurePresentation) :
        RapidConfigurationAction
    data object ServiceRedirectConsumed : RapidConfigurationAction
}

private fun captureRapidConfigurationFailure(error: AppError): String = when (error) {
    AppError.Network -> messageText("nao_foi_possivel_carregar_o_rapi_0e0802")
    is AppError.Validation -> error.reason.ifBlank { messageText("nao_foi_possivel_salvar_o_rapida_58375b") }
    AppError.Unexpected -> messageText("ocorreu_um_erro_inesperado_no_ra_2ee739")
}

private fun captureRapidFavoritesFailure(error: AppError): String = when (error) {
    AppError.Network -> messageText("nao_foi_possivel_carregar_as_apo_e3b519")
    is AppError.Validation -> error.reason.ifBlank { messageText("nao_foi_possivel_carregar_as_apo_0ae4e5") }
    AppError.Unexpected -> messageText("nao_foi_possivel_carregar_as_apo_0ae4e5")
}

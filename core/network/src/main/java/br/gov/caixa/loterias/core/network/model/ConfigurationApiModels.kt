package br.gov.caixa.loterias.core.network.model

import com.google.gson.annotations.SerializedName

data class AppParametersMapDto(
    @SerializedName("mapa") val values: AppParametersDto? = null,
)

data class AppParametersDto(
    @SerializedName("qtdeMinimaApostaBolaoFiltro") val minimumMarketplaceBetQuantity: Int? = null,
    @SerializedName("qtdeMaximaApostaBolaoFiltro") val maximumMarketplaceBetQuantity: Int? = null,
    @SerializedName(value = "habilitarConferenciaAutomaticaApostas", alternate = ["microServico"]) val microserviceEnabled: Boolean? = null,
    @SerializedName(value = "hostMicroServicoConferenciaAposta", alternate = ["urlBase"]) val betsHistoryBaseUrl: String? = null,
    @SerializedName(value = "hostNovoMicroServicoConferenciaApostaProducao", alternate = ["urlBaseNovaApiPRD"]) val productionBetsApiBaseUrl: String? = null,
    @SerializedName(value = "hostNovoMicroServicoConferenciaApostaPiloto", alternate = ["urlBaseNovaApiPLT"]) val pilotBetsApiBaseUrl: String? = null,
    @SerializedName(value = "grupoCpfsAcessamNovoConferenciaAposta", alternate = ["grupoCpfNovaApi"]) val novaApiCpfRolloutPercentage: Int? = null,
    @SerializedName(value = "hostMicroServicoCarrinhoPiloto", alternate = ["urlBaseCarrinhoPiloto"]) val pilotCartBaseUrl: String? = null,
    @SerializedName(value = "hostMicroServicoCarrinhoProducao", alternate = ["urlBaseCarrinhoProducao"]) val productionCartBaseUrl: String? = null,
    @SerializedName(value = "hostMicroServicoApostadorProducao", alternate = ["urlBaseApostador"]) val bettorBaseUrl: String? = null,
    @SerializedName("habilitaRevendaBolaoApp") val poolSalesEnabled: Boolean? = null,
    @SerializedName("comboApostasHabilitado") val comboBettingEnabled: Boolean? = null,
    @SerializedName("rapidaoHabilitado") val rapidBettingEnabled: Boolean? = null,
    @SerializedName("consultaMinhasApostas") val myBetsVisible: Boolean? = null,
    @SerializedName("vigenciaMesOutubroRosa") val octoberRoseEnabled: Boolean? = null,
    @SerializedName("vigenciaOutubroRosaMegaSena") val octoberRoseMegaSenaEnabled: Boolean? = null,
    // Extensão opcional; servidores legados continuam usando os dois indicadores acima.
    @SerializedName("campanhasComemorativas") val commemorativeCampaigns: List<CommemorativeCampaignDto?>? = null,
    @SerializedName("nuvemApikey") val cloudSubscriptionKey: String? = null,
    @SerializedName("concursoMega30Anos") val megaThirtyYearsContest: Int? = null,
    @SerializedName("concursoLotecaPais") val lotecaFathersDayContest: Int? = null,
)

data class CommemorativeCampaignDto(
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("ativa") val active: Boolean? = null,
    @SerializedName("areas") val areas: List<String?>? = null,
    @SerializedName("inicio") val startsOn: String? = null,
    @SerializedName("fim") val endsOn: String? = null,
    @SerializedName("prioridade") val priority: Int? = null,
)

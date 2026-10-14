package br.gov.caixa.loterias.core.network

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement
import java.math.BigDecimal

data class ApiEnvelope<T>(
    @SerializedName("payload") val payload: T? = null,
    @SerializedName("redirect") val redirect: String? = null,
    @SerializedName("mensagem") val message: String? = null,
    @SerializedName("dataHoraServidor") val serverDateTime: String? = null,
)

data class PrizeRedemptionEnvelopeDto(
    @SerializedName("payload") val payload: JsonElement? = null,
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: String? = null,
    @SerializedName("mensagem") val message: String? = null,
) {
    override fun toString(): String = "PrizeRedemptionEnvelopeDto(redacted)"
}

data class MercadoPagoRegistrationEnvelopeDto(
    @SerializedName("payload") val payload: String? = null,
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: String? = null,
) {
    override fun toString(): String = "MercadoPagoRegistrationEnvelopeDto(redacted)"
}

data class FavoriteBetEnumDto(
    @SerializedName("valor") val value: Int? = null,
    @SerializedName("descricao") val description: String? = null,
)

data class FavoriteBetNamedParameterDto(
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("uf") val state: String? = null,
)

data class FavoriteBetCloversDto(
    @SerializedName("trevosSelecionados") val selected: JsonElement? = null,
)

data class FavoriteBetDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("modalidade") val modality: FavoriteBetEnumDto? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("numerosSelecionados") val selectedNumbers: JsonElement? = null,
    @SerializedName("timeDoCoracao") val heartTeam: FavoriteBetNamedParameterDto? = null,
    @SerializedName("mesDeSorte") val luckyMonth: FavoriteBetNamedParameterDto? = null,
    @SerializedName("trevos") val clovers: FavoriteBetCloversDto? = null,
)

data class FavoriteBetPageDto(
    @SerializedName("rowCount") val rowCount: Long? = null,
    @SerializedName("lista") val items: List<FavoriteBetDto>? = null,
    @SerializedName("listaSize") val itemCount: Int? = null,
)

data class GroupedFavoriteBetsDto(
    @SerializedName("resultado") val groups: Map<String, FavoriteBetPageDto>? = null,
    @SerializedName("pageSize") val pageSize: Int? = null,
)

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
    @SerializedName("nuvemApikey") val cloudSubscriptionKey: String? = null,
)

data class SupportSectionDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("duvidas") val questions: List<SupportQuestionDto>? = null,
)

data class SupportQuestionDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("pergunta") val question: String? = null,
    @SerializedName("resposta") val answer: String? = null,
)

data class TermsDto(
    @SerializedName("conteudo") val content: String? = null,
)

data class MarketplacePageDto(
    @SerializedName("totalRegistros") val totalRecords: Int? = null,
    @SerializedName("paginaAtual") val currentPage: Int? = null,
    @SerializedName("ultimaPagina") val lastPage: Int? = null,
    @SerializedName("cotas") val pools: List<MarketplacePoolDto>? = null,
)

data class MarketplacePoolDto(
    @SerializedName("codigoBolao") val code: String? = null,
    @SerializedName("modalidade") val modality: String? = null,
    @SerializedName("concurso") val contest: Int? = null,
    @SerializedName("nomeFantasia") val lotteryName: String? = null,
    @SerializedName("dataSorteio") val drawDate: String? = null,
    @SerializedName("valorCota") val sharePrice: String? = null,
    @SerializedName("qtdCotaDisponivel") val availableShares: Int? = null,
    @SerializedName("qtdApostas") val betQuantity: Int? = null,
    @SerializedName("qtdNumeros") val numberQuantity: Int? = null,
    @SerializedName("lotericaFavorita") val favoriteLottery: Boolean? = null,
    @SerializedName("loterica") val lotteryId: Long? = null,
    @SerializedName(value = "municipio", alternate = ["municipioCota"]) val city: MarketplaceCityDto? = null,
    @SerializedName("uf") val state: MarketplaceStateDto? = null,
    @SerializedName("qtdCotaTotal") val totalShares: Int? = null,
    @SerializedName("tipoConcurso") val contestType: Int? = null,
    @SerializedName("vrPremioEstimado") val estimatedPrize: String? = null,
    @SerializedName("vrCotaComTarifa") val regularSharePriceWithFee: BigDecimal? = null,
    @SerializedName("valorTarifaServico") val serviceFeePerShare: BigDecimal? = null,
    @SerializedName("vrUltimaCotaComTarifa") val lastSharePriceWithFee: BigDecimal? = null,
)

data class MarketplacePoolDetailDto(
    @SerializedName("qtdCotaTotal") val totalShares: Int? = null,
    @SerializedName("qtdCotaDisponivel") val availableShares: Int? = null,
    @SerializedName("loterica") val lotteryId: Long? = null,
    @SerializedName("nomeFantasia") val lotteryName: String? = null,
    @SerializedName("municipioCota") val city: MarketplaceCityDto? = null,
    @SerializedName("concurso") val contest: Int? = null,
    @SerializedName("tipoConcurso") val contestType: Int? = null,
    @SerializedName("modalidade") val modality: String? = null,
    @SerializedName("dataSorteio") val drawDate: String? = null,
    @SerializedName("diaSorteio") val drawDay: String? = null,
    @SerializedName("horaSorteio") val drawTime: String? = null,
    @SerializedName("vrPremioEstimado") val estimatedPrize: String? = null,
    @SerializedName("qtdApostas") val betQuantity: Int? = null,
    @SerializedName("qtdNumeros") val numberQuantity: Int? = null,
    @SerializedName("vrCotaSemTarifa") val sharePriceWithoutFee: String? = null,
    @SerializedName("vrTarifaServicoCota") val serviceFeePerShare: BigDecimal? = null,
    @SerializedName("vrUltimaCotaComTarifa") val lastSharePriceWithFee: String? = null,
    @SerializedName("vrTarifaServicoUltimaCota") val lastShareFee: String? = null,
    @SerializedName("lotericaFormatada") val formattedLottery: MarketplaceLotteryDto? = null,
    @SerializedName("apostas") val bets: List<MarketplaceBetDto>? = null,
)

data class MarketplaceCityDto(
    @SerializedName("nome") val name: String? = null,
    @SerializedName("uf") val state: MarketplaceStateDto? = null,
)

data class MarketplaceStateDto(
    @SerializedName("sigla") val abbreviation: String? = null,
)

data class MarketplaceLotteryDto(
    @SerializedName("numeroFormatado") val formattedNumber: String? = null,
)

data class MarketplaceBetDto(
    @SerializedName("indicadorSurpresinha") val surprise: Boolean? = null,
    // Some modalities return a flat list, while Super Sete/Loteca may return matrices.
    @SerializedName("dezenas") val numbers: JsonElement? = null,
    @SerializedName("trevos") val clovers: List<Int>? = null,
)

data class DrawResultDto(
    @SerializedName("municipioSorteio") val city: MarketplaceCityDto? = null,
    @SerializedName("ufSorteio") val state: MarketplaceStateDto? = null,
    @SerializedName("aviso") val notice: String? = null,
    @SerializedName("arrecadacaoTotal") val totalRevenue: String? = null,
    @SerializedName("acumuladoParaProximoConcurso") val accumulatedNextContest: String? = null,
    @SerializedName("acumulou") val accumulated: Boolean? = null,
    @SerializedName("numerosSorteadosPrimeiroSorteio") val firstDrawNumbers: JsonElement? = null,
    @SerializedName("numerosSorteadosSegundoSorteio") val secondDrawNumbers: List<Int>? = null,
    @SerializedName("trevosSorteadosPrimeiroSorteio") val clovers: List<Int>? = null,
    @SerializedName("concurso") val contest: DrawContestDto? = null,
    @SerializedName("premiacoesPrimeiroSorteio") val payouts: List<DrawPayoutDto>? = null,
    @SerializedName("premiacoesSegundoSorteio") val secondDrawPayouts: List<DrawPayoutDto>? = null,
    @SerializedName("partidasLoteca") val lotecaMatches: List<DrawSportsMatchDto>? = null,
    @SerializedName("partidasLotogol") val lotogolMatches: List<DrawSportsMatchDto>? = null,
    @SerializedName("premiacaoTimeDoCoracao") val favoriteTeamReward: DrawSelectionRewardDto? = null,
    @SerializedName("premiacaoMesDeSorte") val luckyMonthReward: DrawSelectionRewardDto? = null,
    @SerializedName("ganhadoresPrimeiroSorteio") val firstDrawRegionalWinners: List<DrawRegionalWinnerDto>? = null,
    @SerializedName("ganhadoresSegundoSorteio") val secondDrawRegionalWinners: List<DrawRegionalWinnerDto>? = null,
    @SerializedName("proximoConcurso") val nextContest: DrawContestDto? = null,
    @SerializedName("proximoConcursoFinalZero") val nextFinalZeroContest: DrawContestDto? = null,
    @SerializedName("proximoConcursoFinalCinco") val nextFinalFiveContest: DrawContestDto? = null,
    @SerializedName("proximoConcursoEspecial") val nextSpecialContest: DrawContestDto? = null,
)

data class DrawContestDto(
    @SerializedName("modalidade") val modality: String? = null,
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("dataSorteio") val drawDate: String? = null,
    @SerializedName("tipoConcurso") val contestType: String? = null,
    @SerializedName("estimativa") val estimatedPrize: String? = null,
    @SerializedName("modalidadeDetalhada") val modalityDetails: LotteryModalityDto? = null,
)

data class DrawPayoutDto(
    @SerializedName("descricao") val description: String? = null,
    @SerializedName("quantidadeGanhadores") val winners: Long? = null,
    @SerializedName("valor") val value: String? = null,
)

data class DrawSportsTeamDto(
    @SerializedName("parametroEquipe") val parameterTeam: SportsTeamParameterDto? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("descricaoCurta") val shortName: String? = null,
    @SerializedName("uf") val state: String? = null,
    @SerializedName("placar") val score: String? = null,
    @SerializedName("vitoria") val won: Boolean? = null,
)

data class DrawSportsMatchDto(
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("equipe1") val home: DrawSportsTeamDto? = null,
    @SerializedName("equipe2") val away: DrawSportsTeamDto? = null,
    @SerializedName("empate") val draw: Boolean? = null,
    @SerializedName("indexPlacarEquipe1") val homeScoreIndex: Int? = null,
    @SerializedName("indexPlacarEquipe2") val awayScoreIndex: Int? = null,
    @SerializedName("letraLegenda") val legendLetter: String? = null,
    @SerializedName("descricaoLegenda") val legendDescription: String? = null,
)

data class DrawSelectionRewardDto(
    @SerializedName("equipe") val team: SportsTeamParameterDto? = null,
    @SerializedName("mesDeSorte") val month: LuckyMonthParameterDto? = null,
    @SerializedName("quantidadeGanhadores") val winners: Long? = null,
    @SerializedName("valor") val value: String? = null,
)

data class DrawRegionalWinnerDto(
    @SerializedName("municipioSorteio") val city: MarketplaceCityDto? = null,
    @SerializedName("ufSorteio") val state: MarketplaceStateDto? = null,
    @SerializedName("descricao") val description: String? = null,
)

data class SelfAssessmentPayloadDto(
    @SerializedName("retornoPerguntaResposta") val questions: List<SelfAssessmentQuestionDto>? = null,
)

data class SelfAssessmentQuestionDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("descricaoPergunta") val description: String? = null,
    @SerializedName("ordemPergunta") val order: Int? = null,
    @SerializedName("ativo") val active: Boolean? = null,
)

data class SelfAssessmentAnswerDto(
    @SerializedName("idPergunta") val questionId: Int,
    @SerializedName("descricaoResposta") val description: String,
    @SerializedName("indicadorResposta") val affirmative: Boolean,
)

data class SocialTransfersDto(
    @SerializedName("arrecadacaoAcumulada") val accumulatedRevenue: BigDecimal? = null,
    @SerializedName("arrecadacaoUltimaSemana") val lastWeekRevenue: BigDecimal? = null,
    @SerializedName("dataBase") val baseDate: String? = null,
    @SerializedName("percentualProgramasSociais") val socialProgramsPercentage: BigDecimal? = null,
    @SerializedName("percentualEducacao") val educationPercentage: BigDecimal? = null,
    @SerializedName("percentualCultura") val culturePercentage: BigDecimal? = null,
    @SerializedName("percentualEsporte") val sportPercentage: BigDecimal? = null,
    @SerializedName("percentualSeguranca") val securityPercentage: BigDecimal? = null,
    @SerializedName("percentualSeguridade") val socialSecurityPercentage: BigDecimal? = null,
    @SerializedName("percentualOutros") val otherPercentage: BigDecimal? = null,
)

data class UserProfileDto(
    @SerializedName("cpf") val cpf: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("dataNascimento") val birthDate: String? = null,
    @SerializedName("telefone") val phone: PhoneDto? = null,
    @SerializedName("cep") val zipCode: String? = null,
    @SerializedName("aceitaReceberNoticias") val acceptsNotifications: Boolean? = null,
    @SerializedName("municipioId") val municipalityId: MunicipalityIdDto? = null,
    @SerializedName("sexo") val gender: String? = null,
    @SerializedName("limiteMinimoDiario") val minimumDailyLimit: BigDecimal? = null,
    @SerializedName("limiteDiario") val dailyLimit: BigDecimal? = null,
    @SerializedName("limiteDiarioAutorizado") val authorizedDailyLimit: BigDecimal? = null,
)

data class PhoneDto(
    @SerializedName("ddd") val areaCode: String? = null,
    @SerializedName("numero") val number: String? = null,
)

data class MunicipalityIdDto(
    @SerializedName("idUF") val stateId: Long? = null,
    @SerializedName("numero") val number: Long? = null,
    @SerializedName("digitoVerificador") val checkDigit: Long? = null,
)

data class FederativeUnitDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("sigla") val code: String? = null,
    @SerializedName("nome") val name: String? = null,
)

data class MunicipalityDto(
    @SerializedName("id") val id: MunicipalityIdDto? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("uf") val state: FederativeUnitDto? = null,
)

data class NeighborhoodDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("municipio") val municipality: MunicipalityDto? = null,
)

data class PersonalDataUpdateDto(
    @SerializedName("cep") val zipCode: String,
    @SerializedName("municipioId") val municipalityId: MunicipalityIdDto,
    @SerializedName("sexo") val gender: String,
    @SerializedName("alterandoLoterica") val changingRetailer: Boolean = false,
    @SerializedName("limiteDiario") val dailyLimit: BigDecimal,
    @SerializedName("aceitaReceberNoticias") val acceptsNotifications: Boolean,
)

data class AccountRegistrationDto(
    @SerializedName("cep") val zipCode: String,
    @SerializedName("aceitaTermosUso") val acceptsTerms: Boolean,
    @SerializedName("aceitaReceberNoticias") val acceptsNotifications: Boolean,
    @SerializedName("municipioId") val municipalityId: MunicipalityIdDto,
    @SerializedName("sexo") val gender: String,
    @SerializedName("apostas") val bets: List<Any> = emptyList(),
)

data class AccountVerificationRequestDto(
    @SerializedName("apostas") val bets: List<LegacyCartUploadDto> = emptyList(),
) {
    override fun toString() = "AccountVerificationRequestDto(redacted)"
}

data class BlockedAccountVerificationEnvelopeDto(
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: String? = null,
) {
    override fun toString(): String = "BlockedAccountVerificationEnvelopeDto(redacted)"
}

data class AccountSessionDto(
    @SerializedName("nome") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("primeiroNome") val firstName: String? = null,
    @SerializedName("limiteDiario") val dailyLimit: BigDecimal? = null,
    @SerializedName("suspensaoTemporariaApostador") val temporarySuspension: Boolean? = null,
    @SerializedName("responderAutoavaliacao") val requiresSelfAssessment: Boolean? = null,
    @SerializedName("situacaoApostador") val status: AccountStatusDto? = null,
    @SerializedName("carrinho") val cart: CartDto? = null,
    @SerializedName("ultimaNotificacao") val latestNotification: AccountNotificationDto? = null,
    @SerializedName("pagamentoNaoIdentificado") val unidentifiedPayment: Boolean? = null,
) {
    override fun toString() = "AccountSessionDto(redacted)"
}

data class AccountNotificationDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("titulo") val title: String? = null,
    @SerializedName("conteudo") val content: String? = null,
    @SerializedName("imagem") val imageBase64: String? = null,
    @SerializedName("link") val link: String? = null,
) {
    override fun toString() = "AccountNotificationDto(redacted)"
}

data class AccountStatusDto(
    @SerializedName("enum") val kind: String? = null,
)

data class CartDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("valorTotal") val total: BigDecimal? = null,
    @SerializedName("apostasIndividuais") val individualBets: List<CartBetDto>? = null,
    @SerializedName("cotas") val poolShares: List<CartBetDto>? = null,
    @SerializedName("combos") val combos: List<CartComboDto>? = null,
    @SerializedName("msgCotaExcluida") val expiredPoolShareRemoved: Boolean? = null,
    /** Flat summary projection; grouped fields represent the same bets, not additional amounts. */
    @SerializedName("apostas") val bets: List<CartBetDto>? = null,
)

data class CartBetDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("modalidade") val modality: JsonElement? = null,
    @SerializedName("tipoConcurso") val contestType: JsonElement? = null,
    @SerializedName("numerosSelecionados") val selectedNumbers: JsonElement? = null,
    @SerializedName("numerosSelecionadosMaisMilionariaTrevo") val selectedClovers: JsonElement? = null,
    @SerializedName("valor") val amount: BigDecimal? = null,
    @SerializedName("quantidadeApostas") val betQuantity: Int? = null,
    @SerializedName("quantidadeNumeros") val numberQuantity: Int? = null,
    @SerializedName("quantidadeNumerosMaisMilionariaTrevo") val cloverQuantity: Int? = null,
    @SerializedName("quantidadeTeimosinhas") val recurringDraws: Int? = null,
    @SerializedName("concursoAlvo") val targetContest: Int? = null,
    @SerializedName("concursoInicial") val initialContest: Int? = null,
    @SerializedName("indicadorSurpresinha") val surpriseIndicator: JsonElement? = null,
    @SerializedName("surpresinha") val surprise: Boolean? = null,
    @SerializedName("espelho") val mirror: Boolean? = null,
    @SerializedName("geraEspelho") val generateMirror: Boolean? = null,
    @SerializedName("troca") val exchange: Boolean? = null,
    @SerializedName("combo") val combo: Boolean? = null,
    @SerializedName("mesDeSorte") val luckyMonth: JsonElement? = null,
    @SerializedName("timeDoCoracao") val favoriteTeam: JsonElement? = null,
    @SerializedName("partidasLoteca") val lotecaMatches: JsonElement? = null,
    @SerializedName("partidasLotogol") val lotogolMatches: JsonElement? = null,
    @SerializedName("reservaCotaBolao") val poolReservation: CartPoolReservationDto? = null,
)

data class CartPoolReservationDto(
    @SerializedName("codigoBolaoReserva") val poolCode: String? = null,
    @SerializedName("numeroCotaReservada") val shareNumber: Int? = null,
    @SerializedName("vrTotalCota") val total: BigDecimal? = null,
    @SerializedName("qtdCotaTotalBolao") val totalShares: Int? = null,
    @SerializedName("dataHoraExpiracaoReserva") val expiresAt: String? = null,
)

data class CartComboDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("tipoCombo") val type: CartComboTypeDto? = null,
    @SerializedName("apostas") val bets: List<CartBetDto>? = null,
)

data class CartComboTypeDto(
    @SerializedName("nome") val name: String? = null,
    @SerializedName("descricao") val description: String? = null,
)

data class SaveFavoriteCartRequestDto(
    @SerializedName("nome") val name: String,
    @SerializedName("manterSurpresinhas") val keepSurprises: Boolean = true,
)

data class FavoriteCartDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nome") val name: String? = null,
)

data class FavoriteCartBetDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("modalidade") val modality: FavoriteCartModalityDto? = null,
    @SerializedName("numerosSelecionados") val selectedNumbers: JsonElement? = null,
    @SerializedName("quantidadeTeimosinhas") val recurringDraws: Int? = null,
    @SerializedName("espelho") val mirror: Boolean? = null,
    @SerializedName("indicadorSurpresinha") val surpriseIndicator: String? = null,
    @SerializedName("trevos") val clovers: FavoriteCartCloversDto? = null,
    @SerializedName("quantidadeNumeros") val numberCount: Int? = null,
    @SerializedName("quantidadeTrevos") val cloverCount: Int? = null,
    @SerializedName("timeDoCoracao") val favoriteTeam: FavoriteCartTeamDto? = null,
    @SerializedName("mesDeSorte") val luckyMonth: FavoriteCartMonthDto? = null,
)

data class FavoriteCartTeamDto(
    @SerializedName("nome") val name: String? = null,
    @SerializedName("uf") val state: String? = null,
)

data class FavoriteCartMonthDto(@SerializedName("nome") val name: String? = null)

data class FavoriteCartModalityDto(
    @SerializedName("valor") val value: Int? = null,
    @SerializedName("descricao") val description: String? = null,
    @SerializedName("descricaoEspecial") val specialDescription: String? = null,
)

data class FavoriteCartCloversDto(
    @SerializedName("trevosSelecionados") val selected: JsonElement? = null,
)

data class AddFavoriteCartRequestDto(
    @SerializedName("incluirEspecial") val specialSelection: Int,
)

data class RapidConfigurationDto(
    @SerializedName("valorMinimo") val minimumAmount: BigDecimal? = null,
    @SerializedName("valorMaximo") val maximumAmount: BigDecimal? = null,
    @SerializedName("valorMinimoPremioPrincipal") val minimumPrize: BigDecimal? = null,
    @SerializedName("prognosticosObrigatorios") val requiredNumbers: List<Int>? = null,
    @SerializedName("prognosticosProibidos") val excludedNumbers: List<Int>? = null,
    @SerializedName("modalidades") val modalities: List<LotteryModalityDto>? = null,
    @SerializedName("apostasFavoritas") val favoriteBets: List<FavoriteCartBetDto>? = null,
)

data class LotteryModalityDto(
    @SerializedName("valor") val id: Int? = null,
    @SerializedName("descricao") val name: String? = null,
    @SerializedName("descricaoEspecial") val specialName: String? = null,
    @SerializedName("descricaoGestor") val managerName: String? = null,
)

data class BettingParametersDto(
    @SerializedName("valorLimiteDiario") val dailyLimit: BigDecimal? = null,
    @SerializedName("valorMinimoCarrinho") val minimumCartAmount: BigDecimal? = null,
    @SerializedName("quantidadeMaximaApostasCarrinho") val maximumCartBets: Int? = null,
    @SerializedName("parametros") val parameters: List<LotteryParameterDto>? = null,
    @SerializedName("modalidadesDisponiveisCotas") val availablePoolModalities: List<AvailablePoolModalityDto>? = null,
)

data class AvailablePoolModalityDto(
    @SerializedName("modalidade") val modalityCode: String? = null,
    @SerializedName("modalidadeDetalhada") val modality: LotteryModalityDto? = null,
    @SerializedName("tipoConcurso") val contestType: String? = null,
    @SerializedName("concurso") val contestNumber: Int? = null,
    @SerializedName("dataEncerramentoRevenda") val resaleClosingDate: String? = null,
    @SerializedName("horaEncerramentoRevenda") val resaleClosingTime: String? = null,
)

data class LotteryParameterDto(
    @SerializedName("parametroJogo") val game: LotteryGameParameterDto? = null,
    @SerializedName("proximoConcurso") val nextGame: LotteryGameParameterDto? = null,
)

data class LotteryGameParameterDto(
    @SerializedName("concurso") val contest: LotteryContestDto? = null,
    @SerializedName("prognosticoMaximo") val maximumNumber: Int? = null,
    @SerializedName("quantidadeMinima") val minimumSelection: Int? = null,
    @SerializedName("quantidadeMaxima") val maximumSelection: Int? = null,
    @SerializedName("quantidadeSurpresinhas") val maximumSurprises: Int? = null,
    @SerializedName("teimosinhas") val recurringOptions: List<Int>? = null,
    @SerializedName("valoresAposta") val prices: List<LotteryPriceDto>? = null,
    @SerializedName("concursoDisponivelSimulacao") val available: Boolean? = null,
    @SerializedName("concursoCanalAberto") val open: Boolean? = null,
    @SerializedName("trevos") val clovers: LotteryCloverParametersDto? = null,
    @SerializedName("equipes") val teams: List<SportsTeamParameterDto>? = null,
    @SerializedName("partidas") val matches: List<SportsMatchParameterDto>? = null,
    @SerializedName("meses") val luckyMonths: List<LuckyMonthParameterDto>? = null,
    @SerializedName("aceitaEspelho") val acceptsMirror: Boolean? = null,
)

data class LuckyMonthParameterDto(
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("abreviacao") val abbreviation: String? = null,
)

data class SportsTeamParameterDto(
    @SerializedName("indicadorSelecao") val selectionIndicator: Boolean? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("numeroPais") val countryNumber: Int? = null,
    @SerializedName("descricaoCurta") val shortDescription: String? = null,
    @SerializedName("descricaoLonga") val longDescription: String? = null,
    @SerializedName("pais") val country: String? = null,
    @SerializedName("siglaPais") val countryCode: String? = null,
    @SerializedName("uf") val state: String? = null,
    @SerializedName("nomeClass") val className: String? = null,
)

data class SportsMatchParameterDto(
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("equipe1") val home: SportsTeamParameterDto? = null,
    @SerializedName("equipe2") val away: SportsTeamParameterDto? = null,
    @SerializedName("descricaoLegenda") val legendDescription: String? = null,
    @SerializedName("letraLegenda") val legendLetter: String? = null,
)

data class SportsTeamShieldDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("modalidades") val modalities: List<String>? = null,
    @SerializedName("equipe") val team: SportsShieldTeamDto? = null,
    @SerializedName("imagem") val imageBase64: String? = null,
)

data class SportsShieldTeamDto(
    @SerializedName("id") val id: Long? = null,
)

data class LotteryContestDto(
    @SerializedName("modalidade") val modalityCode: String? = null,
    @SerializedName("modalidadeDetalhada") val modality: LotteryModalityDto? = null,
    @SerializedName("tipoConcurso") val contestType: String? = null,
    @SerializedName("numero") val number: Int? = null,
    @SerializedName("dataSorteio") val drawDate: String? = null,
    @SerializedName("dataHoraSorteio") val drawDateTime: String? = null,
    @SerializedName("dataFechamento") val closingDateTime: String? = null,
    @SerializedName("estimativa") val estimatedPrize: BigDecimal? = null,
    @SerializedName("aberto") val open: Boolean? = null,
)

data class LotteryPriceDto(
    @SerializedName("valor") val amount: BigDecimal? = null,
    @SerializedName("numeroPrognosticos") val numberQuantity: Int? = null,
    @SerializedName("numeroTrevos") val cloverQuantity: Int? = null,
    @SerializedName("quantidadeDuplos") val doubleQuantity: Int? = null,
    @SerializedName("quantidadeTriplos") val tripleQuantity: Int? = null,
)

data class LotteryCloverParametersDto(
    @SerializedName("prognosticoMaximoTrevo") val maximumNumber: Int? = null,
    @SerializedName("quantidadeMinimaTrevo") val minimumSelection: Int? = null,
    @SerializedName("quantidadeMaximaTrevo") val maximumSelection: Int? = null,
)

data class BetComboDto(
    @SerializedName("tipoCombo") val type: BetComboTypeDto? = null,
    @SerializedName("modalidadesCombo") val entries: List<BetComboEntryDto>? = null,
    @SerializedName("valor") val amount: BigDecimal? = null,
)

data class BetComboTypeDto(
    @SerializedName("codigo") val id: Int? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("descricao") val description: String? = null,
    @SerializedName("novo") val isNew: Boolean? = null,
)

data class BetComboEntryDto(
    @SerializedName("modalidade") val modality: LotteryModalityDto? = null,
    @SerializedName("tipoConcurso") val contestType: String? = null,
    @SerializedName("quantidadeApostas") val betQuantity: Int? = null,
    @SerializedName("quantidadeTeimosinhas") val recurringDraws: Int? = null,
    @SerializedName("quantidadeDezenas") val numberQuantity: Int? = null,
    @SerializedName("quantidadeDezenasTrevos") val cloverQuantity: Int? = null,
    @SerializedName("numero") val contestNumber: Int? = null,
)

data class DomainCharacterDto(
    @SerializedName("valor") val value: String,
    @SerializedName("descricao") val description: String,
)

data class DomainIntegerDto(
    @SerializedName("valor") val value: Int,
    @SerializedName("descricao") val description: String,
)

data class AddBetRequestDto(
    @SerializedName("modalidade") val modality: String,
    @SerializedName("tipoConcurso") val contestType: DomainCharacterDto,
    @SerializedName("numerosSelecionados") val selectedNumbers: JsonElement,
    @SerializedName("numerosSelecionadosMaisMilionariaTrevo") val selectedClovers: List<Int>,
    @SerializedName("indicadorSurpresinha") val surpriseIndicator: DomainIntegerDto,
    @SerializedName("quantidadeTeimosinhas") val recurringDraws: Int,
    @SerializedName("quantidadeApostas") val quantity: Int,
    @SerializedName("quantidadeNumeros") val numberQuantity: Int,
    @SerializedName("quantidadeNumerosMaisMilionariaTrevo") val cloverQuantity: Int,
    @SerializedName("surpresinha") val surprise: Boolean,
    @SerializedName("timeDoCoracao") val favoriteTeam: SportsBetTeamDto? = null,
    @SerializedName("partidasLoteca") val lotecaMatches: List<LotecaBetMatchDto>? = null,
    @SerializedName("partidasLotogol") val lotogolMatches: List<LotogolBetMatchDto>? = null,
    @SerializedName("mesDeSorte") val luckyMonth: LuckyMonthParameterDto? = null,
    @SerializedName("geraEspelho") val generateMirror: Boolean? = null,
)

data class SportsBetTeamDto(
    @SerializedName("indicadorSelecao") val selectionIndicator: Boolean,
    @SerializedName("nome") val name: String,
    @SerializedName("numero") val number: Int,
    @SerializedName("numeroPais") val countryNumber: Int,
    @SerializedName("descricaoCurta") val shortDescription: String,
    @SerializedName("descricaoLonga") val longDescription: String,
    @SerializedName("pais") val country: String,
    @SerializedName("siglaPais") val countryCode: String,
    @SerializedName("uf") val state: String,
    @SerializedName("nomeClass") val className: String,
    @SerializedName("vitoria") val win: Boolean? = null,
    @SerializedName("placar") val score: String? = null,
)

data class LotecaBetMatchDto(
    @SerializedName("equipe1") val home: SportsBetTeamDto,
    @SerializedName("equipe2") val away: SportsBetTeamDto,
    @SerializedName("empate") val draw: Boolean,
    @SerializedName("numero") val number: Int,
)

data class LotogolBetMatchDto(
    @SerializedName("equipe1") val home: SportsBetTeamDto,
    @SerializedName("equipe2") val away: SportsBetTeamDto,
)

/**
 * Body used by the legacy backend to reserve Marketplace shares in the cart.
 * It intentionally mirrors IdentificaoDeUmBolao instead of overloading the
 * regular-bet model with pool-only fields.
 */
data class AddPoolShareRequestDto(
    @SerializedName("modalidade") val modality: String,
    @SerializedName("tipoConcurso") val contestType: DomainCharacterDto,
    @SerializedName("valor") val amount: BigDecimal,
    @SerializedName("concursoAlvo") val targetContest: Int,
    @SerializedName("dataSorteio") val drawDate: String,
    @SerializedName("indicadorCotaBolao") val poolShare: Boolean = true,
    @SerializedName("subcanalCarrinhoAposta") val cartSubchannel: Int = 1,
    @SerializedName("idBolao") val poolCode: String,
    @SerializedName("qtdCotasBolao") val shareQuantity: Int,
    @SerializedName("valorTarifaServico") val serviceFee: BigDecimal,
)

data class AddComboRequestDto(
    @SerializedName("id") val id: Int,
    @SerializedName("qtdApostas") val quantity: Int,
    @SerializedName("isSurpresinha") val surprise: Boolean,
    @SerializedName("limparCarrinho") val clearCart: Boolean = false,
)

data class SuspendAccountRequestDto(
    @SerializedName("cpfApostador") val cpf: String,
    @SerializedName("periodoSuspensao") val periodHours: Int,
)

data class AccountSuspensionDto(
    @SerializedName("apostadorSuspenso") val active: Boolean? = null,
    @SerializedName("prazoHorasDias") val duration: String? = null,
)

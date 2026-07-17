package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;

public class AnalyticsHelper {

    /**
     * Helper centralizado para padronizar eventos do Firebase Analytics.
     * - Eventos em inglês (ex.: view_screen, click_element)
     * - Parâmetros em PT-BR snake_case (ex.: tela_nome, elemento_id)
     * - Mapa de telas via enum Tela
     * - Mapa de elementos via enum ElementoId
    */

    /** Nome das telas (tela_nome) */
    public enum Tela {
        LOGIN("entrada_app"),
        HOME("home"),
        CART("aposta_carrinho_sucesso"),
        BOLAO_CART("bolao_carrinho_sucesso"),
        BETS_CART("carrinho_apostas"),
        BOLAO("bolao_detalhe"),
        MONTAR_APOSTA("montar_aposta"),
        MEGA_SENA_ESCOLHA_NUMEROS("mega_sena_escolha_numeros"),
        LOTOFACIL_SENA_ESCOLHA_NUMEROS("lotofacil_escolha_numeros");

        public final String nome;
        Tela(String nome) { this.nome = nome; }
    }

    public final class AnalyticsEvents {
        public static final String INTERACTION = "interaction";
        public static final String FLOW_START = "flow_start";
        public static final String FLOW_END    = "flow_end";
        public static final String OPERATION_SUCCESS = "operation_success";
        private AnalyticsEvents() {}
    }

    public static final class AnalyticsFields {
        public static final String APOSTA_SIMPLES = "aposta_simples";
        public static final String BOLAO = "bolao";
        private AnalyticsFields() {}
    }

    public static final class JourneyParams {
        public static final String APOSTAR    = "apostar_loteria";

        private JourneyParams() {}
    }

    public static final class SubJourneyParams {
        public static final String APOSTA_SIMPLES = "montar_aposta";
        public static final String BOLAO          = "montar_bolao";
        public static final String CARRINHO = "carrinho";
        public static final String SELECIONAR = "selecionar_modalidade";
        public static final String ENTRY = "entrada_app";

        private SubJourneyParams() {}
    }

    public static final class ResultParams {
        public static final String SUCCESS   = "sucesso";
        public static final String ERROR     = "error";
        public static final String CANCELLED = "cancelled";

        private ResultParams() {}
    }

    public static final class EventCategoryParams {
        public static final String CTA = "cta";

        private EventCategoryParams() {}
    }

    public static final class EventActionParams {
        public static final String CLICK = "clicar";

        private EventActionParams() {}
    }

    public static final class EventLabelParams {
        public static final String ADD_TO_CART = "adicionar_aposta_carrinho";
        public static final String PAYMENT = "prosseguir_para_pagamento";
        public static final String ADD_BOLAO = "adicionar_bolao_carrinho";
        public static final String SIMPLES  = "iniciar_aposta_simples";

        public static final String BOLAO  = "iniciar_bolao";
        public static final String ENTRY = "entrada_app";

        private EventLabelParams() {}
    }

    public static final class ContentCategoryParams {
        public static final String APOSTA     = "aposta";
        public static final String CARRINHO   = "carrinho";
        public static final String CONFIG = "montar_aposta";

        private ContentCategoryParams() {}
    }

    public static final class StatusParams {
        public static final String SUCESSSO = "sucesso_operacao";
        public static final String APOSTAR = "apostar";

        private StatusParams() {}
    }

    public static final class AnalyticsParams {

        private AnalyticsParams() {}

        public static final String EVENT_CATEGORY = "event_category";
        public static final String EVENT_ACTION = "event_action";
        public static final String EVENT_LABEL = "event_label";

        public static final String JOURNEY = "journey";
        public static final String SUB_JOURNEY = "sub_journey";
        public static final String GAME_MODE = "game_mode";
        public static final String CONTEST_NUMBER = "contest_number";
        public static final String BET_TYPE = "bet_type";
        public static final String FLOW_NAME = "flow_name";
        public static final String ENTRY_POINT = "entry_point";
        public static final String START_TIME_MS = "start_time_ms";
        public static final String RESULT = "result";
        public static final String END_TIME_MS = "end_time_ms";
        public static final String FLOW_DURATION_MS = "flow_duration_ms";

        public static final String CART_TOTAL_VALUE = "cart_total_value";
        public static final String BETS_COUNT = "bets_count";
        public static final String INDIVIDUAL_BETS_COUNT = "individual_bets_count";
        public static final String BOLAO_QUOTES_COUNT = "bolao_bets_count";

        public static final String SELECTED_NUMBERS_COUNT = "selected_numbers_count";
        public static final String SELECTED_SURPRESINHA_COUNT = "selected_surpresinha_count";
        public static final String SELECTED_TEIMOSINHA_COUNT = "selected_teimosinha_count";

        public static final String BET_VALUE = "bet_value";
        public static final String MATCHES_COUNT = "matches_count";
        public static final String SIMPLE_GUESSES_COUNT = "simple_guesses_count";
        public static final String DOUBLE_GUESSES_COUNT = "double_guesses_count";
        public static final String TRIPLE_GUESSES_COUNT = "triple_guesses_count";

        public static final String LOTTERY_SHOP_CODE = "lottery_shop_code";
        public static final String LOTTERY_SHOP_NAME = "lottery_shop_name";
        public static final String LOTTERY_SHOP_CITY = "lottery_shop_city";

        public static final String BOLAO_BETS_COUNT = "bolao_bets_count";
        public static final String BOLAO_NUMBERS_PER_BET = "bolao_numbers_per_bet";
        public static final String BOLAO_GUESSES_COUNT = "bolao_guesses_count";
        public static final String QUOTA_VALUE = "quota_value";
        public static final String SERVICE_FEE = "service_fee";
        public static final String TOTAL_QUOTA_VALUE = "total_quota_value";
        public static final String BOLAO_TOTAL_QUOTAS = "bolao_total_quotas";
        public static final String BOLAO_SELECTED_QUOTAS = "bolao_selected_quotas";

        public static final String CONTENT_CATEGORY = "content_category";
        public static final String OPTION_SELECTED = "option_selected";

        public static final String OPERATION_NAME = "operation_name";

        @Deprecated
        public static final String VALOR_APOSTA = BET_VALUE;
    }

    // ===========================================================
    // SINGLETON
    // ===========================================================

    private static volatile AnalyticsHelper instance;
    private final FirebaseAnalytics analytics;

    private @Nullable String estadoAtual;
    private @Nullable String userIdAtual;

    private @Nullable String currentFlowName;
    private long currentFlowStartTimeMs;

    private static final String PARAM_SCREEN_REFERRER = "screen_referrer";


    private AnalyticsHelper(@NonNull Context appContext) {
        this.analytics = FirebaseAnalytics.getInstance(appContext.getApplicationContext());
        enableAnalytics();
    }

    public static void init(@NonNull Context appContext) {
        if (instance == null) {
            synchronized (AnalyticsHelper.class) {
                if (instance == null) {
                    instance = new AnalyticsHelper(appContext);
                }
            }
        }
    }

    public static AnalyticsHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AnalyticsHelper não inicializado.");
        }
        return instance;
    }

    public void enableAnalytics() {
        analytics.setAnalyticsCollectionEnabled(true);
    }

    public void disableAnalytics() {
        analytics.setAnalyticsCollectionEnabled(false);
    }

    public void setUserId(@Nullable String userId) {
        if ((userIdAtual == null && userId != null) ||
                (userIdAtual != null && !userIdAtual.equals(userId))) {
            analytics.setUserId(userId);
            userIdAtual = userId;
        }
    }

    public void setEstado(@NonNull String estado) {
        if (estadoAtual == null || !estadoAtual.equals(estado)) {
            analytics.setUserProperty("estado", estado);
            estadoAtual = estado;
        }
    }

    public void logViewScreen(
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull Tela tela
    ) {
        Bundle bundle = new Bundle();

        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        bundle.putString(
                PARAM_SCREEN_REFERRER,
                AnalyticsScreenTracker.getLastScreenName()
        );

        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);

        analytics.logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                bundle
        );

        AnalyticsScreenTracker.updateLastScreenName(tela.nome);
    }

    public void logViewScreenAposta(
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull Tela tela,
            @NonNull String gameMode,
            @Nullable String contestNumber
    ) {
        Bundle bundle = new Bundle();

        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        bundle.putString(
                PARAM_SCREEN_REFERRER,
                AnalyticsScreenTracker.getLastScreenName()
        );

        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);
        bundle.putString(AnalyticsParams.GAME_MODE, gameMode);

        if (contestNumber != null) {
            bundle.putString(AnalyticsParams.CONTEST_NUMBER, contestNumber);
        }

        analytics.logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                bundle
        );

        AnalyticsScreenTracker.updateLastScreenName(tela.nome);
    }

    public void logViewScreenApostaValor(
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull Tela tela,
            @NonNull String gameMode,
            @NonNull String valorAposta,
            @Nullable String contestNumber
    ) {
        Bundle bundle = new Bundle();

        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        bundle.putString(
                PARAM_SCREEN_REFERRER,
                AnalyticsScreenTracker.getLastScreenName()
        );

        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);
        bundle.putString(AnalyticsParams.GAME_MODE, gameMode);
        bundle.putString(AnalyticsParams.VALOR_APOSTA, valorAposta);

        if (contestNumber != null) {
            bundle.putString(AnalyticsParams.CONTEST_NUMBER, contestNumber);
        }

        analytics.logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                bundle
        );

        AnalyticsScreenTracker.updateLastScreenName(tela.nome);
    }

    public void logViewCartScreen(
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull Tela tela,
            @NonNull CarrinhoDTO carrinho
    ) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, tela.nome);
        bundle.putString(PARAM_SCREEN_REFERRER, AnalyticsScreenTracker.getLastScreenName());
        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);

        // cart_total_value
        String total = "";
        if (carrinho.getValorTotal() != null) {
            total = ViewUtils.getMoedaFormat(carrinho.getValorTotal());
        }
        bundle.putString(AnalyticsParams.CART_TOTAL_VALUE, total);

        // contagens
        int betsCount = (carrinho.getApostas() != null) ? carrinho.getApostas().size() : 0;
        int individualCount = (carrinho.getApostasIndividuais() != null) ? carrinho.getApostasIndividuais().size() : 0;
        int bolaoQuotesCount = (carrinho.getBoloes() != null) ? carrinho.getBoloes().size() : 0;

        bundle.putInt(AnalyticsParams.BETS_COUNT, betsCount);
        bundle.putInt(AnalyticsParams.INDIVIDUAL_BETS_COUNT, individualCount);
        bundle.putInt(AnalyticsParams.BOLAO_QUOTES_COUNT, bolaoQuotesCount);

        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);

        AnalyticsScreenTracker.updateLastScreenName(tela.nome);
    }

    /** Evento: interaction */
    public void logInteraction(
            @NonNull String eventCategory,
            @NonNull String eventAction,
            @NonNull String eventLabel,
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull Tela tela
    ) {
        Bundle bundle = new Bundle();

        bundle.putString(AnalyticsParams.EVENT_CATEGORY, eventCategory);
        bundle.putString(AnalyticsParams.EVENT_ACTION, eventAction);
        bundle.putString(AnalyticsParams.EVENT_LABEL, eventLabel);

        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);

        analytics.logEvent(
                AnalyticsEvents.INTERACTION,
                bundle
        );
    }

    public void logInteraction(
            @NonNull String eventCategory,
            @NonNull String eventAction,
            @NonNull String eventLabel,
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull Tela tela,
            @NonNull String gameMode,
            @NonNull String contestNumber
    ) {
        Bundle bundle = new Bundle();

        bundle.putString(AnalyticsParams.EVENT_CATEGORY, eventCategory);
        bundle.putString(AnalyticsParams.EVENT_ACTION, eventAction);
        bundle.putString(AnalyticsParams.EVENT_LABEL, eventLabel);

        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);
        bundle.putString(AnalyticsParams.GAME_MODE, gameMode);
        bundle.putString(AnalyticsParams.CONTEST_NUMBER, contestNumber);

        analytics.logEvent(
                AnalyticsEvents.INTERACTION,
                bundle
        );
    }

    public void logInteraction(
            @NonNull String eventCategory,
            @NonNull String eventAction,
            @NonNull String eventLabel,
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull Tela tela,
            @NonNull String gameMode,
            @NonNull String contestNumber,
            @NonNull String selectedSurpresinhaCount,
            @NonNull String selectedNumbersCount,
            @NonNull String selectedTeimosinhaCount,
            @NonNull String betValue
    ) {
        Bundle bundle = new Bundle();

        bundle.putString(AnalyticsParams.EVENT_CATEGORY, eventCategory);
        bundle.putString(AnalyticsParams.EVENT_ACTION, eventAction);
        bundle.putString(AnalyticsParams.EVENT_LABEL, eventLabel);

        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);

        bundle.putString(AnalyticsParams.GAME_MODE, gameMode);
        bundle.putString(AnalyticsParams.CONTEST_NUMBER, contestNumber);

        bundle.putString(AnalyticsParams.SELECTED_NUMBERS_COUNT, selectedNumbersCount);
        bundle.putString(AnalyticsParams.SELECTED_SURPRESINHA_COUNT, selectedSurpresinhaCount);
        bundle.putString(AnalyticsParams.SELECTED_TEIMOSINHA_COUNT, selectedTeimosinhaCount);
        bundle.putString(AnalyticsParams.BET_VALUE, betValue);
        bundle.putString(AnalyticsParams.BET_TYPE, AnalyticsFields.APOSTA_SIMPLES);

        analytics.logEvent(
                AnalyticsEvents.INTERACTION,
                bundle
        );
    }

    public void logInteraction(
            @NonNull String eventCategory,
            @NonNull String eventAction,
            @NonNull String eventLabel,
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull Tela tela,
            @NonNull String cartTotalValue,
            @NonNull String betsCount,
            @NonNull String individualBetsCount,
            @NonNull String bolaoQuotesCount
    ) {
        Bundle bundle = new Bundle();

        bundle.putString(AnalyticsParams.EVENT_CATEGORY, eventCategory);
        bundle.putString(AnalyticsParams.EVENT_ACTION, eventAction);
        bundle.putString(AnalyticsParams.EVENT_LABEL, eventLabel);

        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);

        bundle.putString(AnalyticsParams.CART_TOTAL_VALUE, cartTotalValue);
        bundle.putString(AnalyticsParams.BETS_COUNT, betsCount);
        bundle.putString(AnalyticsParams.INDIVIDUAL_BETS_COUNT, individualBetsCount);
        bundle.putString(AnalyticsParams.BOLAO_QUOTES_COUNT, bolaoQuotesCount);

        analytics.logEvent(
                AnalyticsEvents.INTERACTION,
                bundle
        );
    }

    public void logInteraction(
            @NonNull String eventCategory,
            @NonNull String eventAction,
            @NonNull String eventLabel,
            @NonNull Tela tela,
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull String gameMode,
            @NonNull String contestNumber,
            @NonNull String matchesCount,
            @NonNull String simpleGuessesCount,
            @NonNull String doubleGuessesCount,
            @NonNull String tripleGuessesCount,
            @NonNull String betValue
    ) {
        Bundle bundle = new Bundle();

        bundle.putString(AnalyticsParams.EVENT_CATEGORY, eventCategory);
        bundle.putString(AnalyticsParams.EVENT_ACTION, eventAction);
        bundle.putString(AnalyticsParams.EVENT_LABEL, eventLabel);

        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);

        bundle.putString(AnalyticsParams.GAME_MODE, gameMode);
        bundle.putString(AnalyticsParams.CONTEST_NUMBER, contestNumber);

        bundle.putString(AnalyticsParams.MATCHES_COUNT, matchesCount);
        bundle.putString(AnalyticsParams.SIMPLE_GUESSES_COUNT, simpleGuessesCount);
        bundle.putString(AnalyticsParams.DOUBLE_GUESSES_COUNT, doubleGuessesCount);
        bundle.putString(AnalyticsParams.TRIPLE_GUESSES_COUNT, tripleGuessesCount);
        bundle.putString(AnalyticsParams.BET_VALUE, betValue);
        bundle.putString(AnalyticsParams.BET_TYPE, AnalyticsFields.APOSTA_SIMPLES);

        analytics.logEvent(
                AnalyticsEvents.INTERACTION,
                bundle
        );
    }

    public void logInteractionAddBolao(
            @NonNull Tela tela,
            @NonNull String gameMode,
            @NonNull String eventCategory,
            @NonNull String eventAction,
            @NonNull String eventLabel,
            @NonNull String journey,
            @NonNull String subjourney,
            @NonNull Long lotteryShopCode,
            @NonNull String lotteryShopName,
            @NonNull String lotteryShopCity,
            @NonNull String bolaoBetsCount,
            @NonNull String bolaoNumbersPerBet,
            @NonNull String quotaValue,
            @NonNull String serviceFee,
            @NonNull String totalQuotaValue,
            @NonNull String bolaoTotalQuotas,
            @NonNull String bolaoSelectedQuotas,
            @NonNull String contestNumber
            ) {
        Bundle bundle = new Bundle();

        bundle.putString(AnalyticsParams.EVENT_CATEGORY, eventCategory);
        bundle.putString(AnalyticsParams.EVENT_ACTION, eventAction);
        bundle.putString(AnalyticsParams.EVENT_LABEL, eventLabel);

        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subjourney);

        bundle.putString(AnalyticsParams.GAME_MODE, gameMode);

        bundle.putLong(AnalyticsParams.LOTTERY_SHOP_CODE, lotteryShopCode);
        bundle.putString(AnalyticsParams.LOTTERY_SHOP_NAME, lotteryShopName);
        bundle.putString(AnalyticsParams.LOTTERY_SHOP_CITY, lotteryShopCity);

        bundle.putString(AnalyticsParams.BOLAO_BETS_COUNT, bolaoBetsCount);
        bundle.putString(AnalyticsParams.BOLAO_NUMBERS_PER_BET, bolaoNumbersPerBet);
        bundle.putString(AnalyticsParams.QUOTA_VALUE, quotaValue);
        bundle.putString(AnalyticsParams.SERVICE_FEE, serviceFee);
        bundle.putString(AnalyticsParams.TOTAL_QUOTA_VALUE, totalQuotaValue);
        bundle.putString(AnalyticsParams.BOLAO_TOTAL_QUOTAS, bolaoTotalQuotas);
        bundle.putString(AnalyticsParams.BOLAO_SELECTED_QUOTAS, bolaoSelectedQuotas);
        bundle.putString(AnalyticsParams.BET_TYPE,AnalyticsFields.BOLAO);
        bundle.putString(AnalyticsParams.CONTEST_NUMBER, contestNumber);

        analytics.logEvent(
                AnalyticsEvents.INTERACTION,
                bundle
        );
    }

    public void logInteractionAddLoteca(
            @NonNull Tela tela,
            @NonNull String gameMode,
            @NonNull String eventCategory,
            @NonNull String eventAction,
            @NonNull String eventLabel,
            @NonNull String journey,
            @NonNull String subjourney,
            @NonNull Long lotteryShopCode,
            @NonNull String lotteryShopName,
            @NonNull String lotteryShopCity,
            @NonNull String bolaoBetsCount,
            @NonNull String bolaoNumbersPerBet,
            @NonNull String quotaValue,
            @NonNull String serviceFee,
            @NonNull String totalQuotaValue,
            @NonNull String bolaoTotalQuotas,
            @NonNull String bolaoSelectedQuotas,
            @NonNull String contestNumber
    ) {
        Bundle bundle = new Bundle();

        bundle.putString(AnalyticsParams.EVENT_CATEGORY, eventCategory);
        bundle.putString(AnalyticsParams.EVENT_ACTION, eventAction);
        bundle.putString(AnalyticsParams.EVENT_LABEL, eventLabel);

        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subjourney);

        bundle.putString(AnalyticsParams.GAME_MODE, gameMode);

        bundle.putLong(AnalyticsParams.LOTTERY_SHOP_CODE, lotteryShopCode);
        bundle.putString(AnalyticsParams.LOTTERY_SHOP_NAME, lotteryShopName);
        bundle.putString(AnalyticsParams.LOTTERY_SHOP_CITY, lotteryShopCity);

        bundle.putString(AnalyticsParams.BOLAO_BETS_COUNT, bolaoBetsCount);
        bundle.putString(AnalyticsParams.BOLAO_GUESSES_COUNT, bolaoNumbersPerBet);
        bundle.putString(AnalyticsParams.QUOTA_VALUE, quotaValue);
        bundle.putString(AnalyticsParams.SERVICE_FEE, serviceFee);
        bundle.putString(AnalyticsParams.TOTAL_QUOTA_VALUE, totalQuotaValue);
        bundle.putString(AnalyticsParams.BOLAO_TOTAL_QUOTAS, bolaoTotalQuotas);
        bundle.putString(AnalyticsParams.BOLAO_SELECTED_QUOTAS, bolaoSelectedQuotas);
        bundle.putString(AnalyticsParams.BET_TYPE,AnalyticsFields.BOLAO);
        bundle.putString(AnalyticsParams.CONTEST_NUMBER, contestNumber);

        analytics.logEvent(
                AnalyticsEvents.INTERACTION,
                bundle
        );
    }

    public void logSelectContent(
            @NonNull Tela tela,
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull String contentCategory,
            @NonNull String gameMode,
            String optionSelected
    ) {
        Bundle bundle = new Bundle();

        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);

        bundle.putString(AnalyticsParams.GAME_MODE, gameMode);

        bundle.putString(AnalyticsParams.CONTENT_CATEGORY, contentCategory);
        bundle.putString(AnalyticsParams.OPTION_SELECTED, optionSelected);

        analytics.logEvent(
                FirebaseAnalytics.Event.SELECT_CONTENT,
                bundle
        );
    }

    public void logOperationSuccess(
            @NonNull String operationName,
            @NonNull Tela tela,
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull String gameMode,
            @NonNull String contestNumber,
            @NonNull String betValue,
            @NonNull Boolean isBolao,
            @NonNull String eventType
    ) {
        long endTime = System.currentTimeMillis();
        long duration = currentFlowStartTimeMs > 0
                ? endTime - currentFlowStartTimeMs
                : 0L;

        Bundle bundle = new Bundle();

        bundle.putString(AnalyticsParams.OPERATION_NAME, operationName);

        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);

        bundle.putString(AnalyticsParams.GAME_MODE, gameMode);
        bundle.putString(AnalyticsParams.CONTEST_NUMBER, contestNumber);

        if (isBolao) {
            bundle.putString(AnalyticsParams.BET_TYPE,AnalyticsFields.BOLAO);
        } else {
            bundle.putString(AnalyticsParams.BET_TYPE, AnalyticsFields.APOSTA_SIMPLES);
        }
        bundle.putString(AnalyticsParams.BET_VALUE, betValue);
        bundle.putLong(AnalyticsParams.END_TIME_MS, endTime);
        bundle.putLong(AnalyticsParams.FLOW_DURATION_MS, duration);

        analytics.logEvent(
                AnalyticsEvents.OPERATION_SUCCESS,
                bundle
        );
    }

    /** Evento: flow_start */
    public void logFlowStart(
            @NonNull String flowName,
            @NonNull String entryPoint,
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull Tela tela
    ) {
        long startTime = System.currentTimeMillis();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, tela.nome);
        bundle.putString(AnalyticsParams.FLOW_NAME, flowName);
        bundle.putString(AnalyticsParams.ENTRY_POINT, entryPoint);
        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);
        bundle.putLong(AnalyticsParams.START_TIME_MS, startTime);

        analytics.logEvent(AnalyticsEvents.FLOW_START, bundle);

        // guarda estado do fluxo
        currentFlowName = flowName;
        currentFlowStartTimeMs = startTime;
    }

    public void logFlowEnd(
            @NonNull String flowName,
            @NonNull String journey,
            @NonNull String subJourney,
            @NonNull Tela tela,
            @Nullable CarrinhoDTO carrinho
    ) {
        if (currentFlowName == null || currentFlowStartTimeMs == 0L) {
            // não existe flow_start → evita dado corrupto
            return;
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - currentFlowStartTimeMs;

        Bundle bundle = new Bundle();

        bundle.putString(AnalyticsParams.FLOW_NAME, flowName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, tela.nome);
        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);

        bundle.putLong(AnalyticsParams.END_TIME_MS, endTime);
        bundle.putLong(AnalyticsParams.FLOW_DURATION_MS, duration);

        if (carrinho != null) {
            if (carrinho.getValorTotal() != null) {
                bundle.putDouble(
                        AnalyticsParams.CART_TOTAL_VALUE,
                        carrinho.getValorTotal().doubleValue()
                );
            }

            if (carrinho.getApostas() != null) {
                bundle.putInt(
                        AnalyticsParams.BETS_COUNT,
                        carrinho.getApostas().size()
                );
            }
        }

        analytics.logEvent(AnalyticsEvents.FLOW_END, bundle);

        currentFlowName = null;
        currentFlowStartTimeMs = 0L;
    }

}

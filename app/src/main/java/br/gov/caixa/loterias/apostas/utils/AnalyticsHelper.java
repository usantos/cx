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
        CART("add_to_cart_success"),
        BETS_CART("bets_cart"),
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
        private AnalyticsEvents() {}
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
        public static final String SUCCESS   = "success";
        public static final String ERROR     = "error";
        public static final String CANCELLED = "cancelled";

        private ResultParams() {}
    }

    public static final class EventCategoryParams {
        public static final String CTA = "cta";

        private EventCategoryParams() {}
    }

    public static final class EventActionParams {
        public static final String CLICAR = "clicar";

        private EventActionParams() {}
    }

    public static final class EventLabelParams {
        public static final String ADD_TO_CART = "adicionar_aposta_carrinho";
        public static final String ADD_BOLAO = "adicionar_bolao_carrinho";
        public static final String SIMPLES  = "iniciar_aposta_simples";

        public static final String BOLAO  = "iniciar_bolao";
        public static final String ENTRY = "entrar_no_app";

        private EventLabelParams() {}
    }

    public static final class ContentCategoryParams {
        public static final String APOSTA     = "aposta";
        public static final String CARRINHO   = "carrinho";
        public static final String CONFIG = "bet_configuration";

        private ContentCategoryParams() {}
    }

    public static final class StatusParams {
        public static final String LOGIN = "login";

        private StatusParams() {}
    }

    public static final class AnalyticsParams {
        public static final String EVENT_CATEGORY = "event_category";
        public static final String EVENT_ACTION   = "event_action";
        public static final String EVENT_LABEL    = "event_label";
        public static final String JOURNEY        = "journey";
        public static final String SUB_JOURNEY    = "sub_journey";
        public static final String FLOW_NAME     = "flow_name";
        public static final String ENTRY_POINT   = "entry_point";
        public static final String START_TIME_MS = "start_time_ms";
        public static final String RESULT            = "result";
        public static final String END_TIME_MS       = "end_time_ms";
        public static final String FLOW_DURATION_MS  = "flow_duration_ms";
        public static final String CART_TOTAL_VALUE        = "cart_total_value";
        public static final String BETS_COUNT              = "bets_count";
        public static final String INDIVIDUAL_BETS_COUNT   = "individual_bets_count";
        public static final String BOLAO_QUOTES_COUNT      = "bolao_quotes_count";


        private AnalyticsParams() {}
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

        bundle.putString("journey", journey);
        bundle.putString("sub_journey", subJourney);

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

        bundle.putString("journey", journey);
        bundle.putString("sub_journey", subJourney);
        bundle.putString("game_mode", gameMode);

        if (contestNumber != null) {
            bundle.putString("contest_number", contestNumber);
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

        bundle.putString("journey", journey);
        bundle.putString("sub_journey", subJourney);
        bundle.putString("game_mode", gameMode);
        bundle.putString("value", valorAposta);

        if (contestNumber != null) {
            bundle.putString("contest_number", contestNumber);
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
        double total = 0d;
        if (carrinho.getValorTotal() != null) {
            total = carrinho.getValorTotal().doubleValue();
        }
        bundle.putDouble(AnalyticsParams.CART_TOTAL_VALUE, total);

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
        bundle.putString("game_mode", gameMode);
        bundle.putString("contest_number", contestNumber);

        analytics.logEvent(
                AnalyticsEvents.INTERACTION,
                bundle
        );
    }

    public void logInteraction(
            @NonNull String eventCategory,               // cta
            @NonNull String eventAction,                 // clicar
            @NonNull String eventLabel,                  // adicionar_aposta_carrinho
            @NonNull String journey,                     // apostar_loteria
            @NonNull String subJourney,                  // montar_aposta
            @NonNull Tela tela,                          // MEGA_SENA_MONTAR_APOSTA
            @NonNull String gameMode,                    // mega_sena
            @NonNull String contestNumber,               // <current_contest_number>
            @NonNull String selectedSurpresinhaCount,        // <current_selected_surpresinha_count>
            @NonNull String selectedNumbersCount,        // <current_selected_numbers_count>
            @NonNull String selectedTeimosinhaCount,     // <current_selected_teimosinha_count>
            @NonNull String betValue                     // <current_bet_value>
    ) {
        Bundle bundle = new Bundle();

        // interaction base
        bundle.putString(AnalyticsParams.EVENT_CATEGORY, eventCategory);
        bundle.putString(AnalyticsParams.EVENT_ACTION, eventAction);
        bundle.putString(AnalyticsParams.EVENT_LABEL, eventLabel);

        // tela
        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        // jornada
        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);

        // contexto da aposta
        bundle.putString("game_mode", gameMode);
        bundle.putString("contest_number", contestNumber);

        bundle.putString("selected_numbers_count", selectedNumbersCount);
        bundle.putString("selected_surpresinha_count", selectedSurpresinhaCount);
        bundle.putString("selected_teimosinha_count", selectedTeimosinhaCount);
        bundle.putString("bet_value", betValue);

        analytics.logEvent(
                AnalyticsEvents.INTERACTION,
                bundle
        );
    }

    public void logInteractionAddBolao(
            @NonNull Tela tela,                          // MEGA_SENA_BOLAO_DETALHE
            @NonNull String gameMode,                    // mega_sena
            @NonNull String eventCategory,               // cta
            @NonNull String eventAction,                 // clicar
            @NonNull String eventLabel,                  // adicionar_aposta_carrinho
            @NonNull String journey,
            @NonNull String subjourney,

            // lotérica
            @NonNull Long lotteryShopCode,
            @NonNull String lotteryShopName,
            @NonNull String lotteryShopCity,

            // dados do bolão
            @NonNull String bolaoBetsCount,
            @NonNull String bolaoNumbersPerBet,
            @NonNull String quotaValue,
            @NonNull String serviceFee,
            @NonNull String totalQuotaValue,
            @NonNull String bolaoTotalQuotas,
            @NonNull String bolaoSelectedQuotas
    ) {
        Bundle bundle = new Bundle();

        // interaction base
        bundle.putString(AnalyticsParams.EVENT_CATEGORY, eventCategory);
        bundle.putString(AnalyticsParams.EVENT_ACTION, eventAction);
        bundle.putString(AnalyticsParams.EVENT_LABEL, eventLabel);

        // tela
        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        // jornada
        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subjourney);

        // contexto
        bundle.putString("game_mode", gameMode);

        bundle.putLong("lottery_shop_code", lotteryShopCode);
        bundle.putString("lottery_shop_name", lotteryShopName);
        bundle.putString("lottery_shop_city", lotteryShopCity);

        bundle.putString("bolao_bets_count", bolaoBetsCount);
        bundle.putString("bolao_numbers_per_bet", bolaoNumbersPerBet);
        bundle.putString("quota_value", quotaValue);
        bundle.putString("service_fee", serviceFee);
        bundle.putString("total_quota_value", totalQuotaValue);
        bundle.putString("bolao_total_quotas", bolaoTotalQuotas);
        bundle.putString("bolao_selected_quotas", bolaoSelectedQuotas);

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
            String optionSelected,
            String optionSource
    ) {
        Bundle bundle = new Bundle();

        // screen
        bundle.putString(
                FirebaseAnalytics.Param.SCREEN_NAME,
                tela.nome
        );

        // journey
        bundle.putString("journey", journey);
        bundle.putString("sub_journey", subJourney);

        //game mode
        bundle.putString("game_mode", gameMode);

        // conteúdo
        bundle.putString("content_category", contentCategory);
        bundle.putString("option_selected", optionSelected);
        bundle.putString("option_source", optionSource);

        analytics.logEvent(
                FirebaseAnalytics.Event.SELECT_CONTENT,
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
            @NonNull String result, // success | error | abandon
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

        bundle.putString(AnalyticsParams.FLOW_NAME, currentFlowName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, tela.nome);
        bundle.putString(AnalyticsParams.JOURNEY, journey);
        bundle.putString(AnalyticsParams.SUB_JOURNEY, subJourney);
        bundle.putString(AnalyticsParams.RESULT, result);

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

        // ✅ limpa estado do fluxo
        currentFlowName = null;
        currentFlowStartTimeMs = 0L;
    }

}

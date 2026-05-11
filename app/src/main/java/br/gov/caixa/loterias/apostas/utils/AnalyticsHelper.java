package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

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
        MONTAR_APOSTA("montar_aposta"),
        BOLAO("comprar_bolao"),
        MEGA_SENA_ESCOLHA_NUMEROS("mega_sena_escolha_numeros"),
        LOTOFACIL_SENA_ESCOLHA_NUMEROS("lotofacil_escolha_numeros");

        public final String nome;
        Tela(String nome) { this.nome = nome; }
    }

    public final class AnalyticsEvents {
        public static final String INTERACTION = "interaction";
        public static final String FLOW_START = "flow_start";
        private AnalyticsEvents() {}
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


        private AnalyticsParams() {}
    }


    /** Identificadores de elementos clicáveis (elemento_id) */
    public enum ElementoId {
        SELECIONAR_NUMERO("selecionar_numero"),
        COMPLETAR_NUMEROS_ALEATORIOS("completar_com_numeros_aleatorios"),
        SALVAR_APOSTA("salvar_aposta"),
        LIMPAR_APOSTA("limpar_aposta"),
        ADICIONAR_AO_CARRINHO("adicionar_ao_carrinho"),
        VOLTAR("voltar");

        public final String id;
        ElementoId(String id) { this.id = id; }
    }

    // ===========================================================
    // SINGLETON
    // ===========================================================

    private static volatile AnalyticsHelper instance;
    private final FirebaseAnalytics analytics;

    private @Nullable String estadoAtual;
    private @Nullable String userIdAtual;

    public static final String EVENT_VIEW_SCREEN = "view_screen";
    public static final String EVENT_CLICK_ELEMENT = "click_element";

    public static final String PARAM_JORNADA_NOME = "jornada_nome";
    public static final String PARAM_SUBJORNADA = "subjornada";
    public static final String PARAM_TELA_NOME = "tela_nome";
    public static final String PARAM_ELEMENTO_ID = "elemento_id";
    public static final String PARAM_CONTEUDO = "conteudo";
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

    /** Evento: flow_start */
    public void logFlowStart(
            @NonNull String flowName,
            @NonNull String entryPoint,
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
                AnalyticsParams.FLOW_NAME,
                flowName
        );

        bundle.putString(
                AnalyticsParams.ENTRY_POINT,
                entryPoint
        );

        bundle.putString(
                AnalyticsParams.JOURNEY,
                journey
        );

        bundle.putString(
                AnalyticsParams.SUB_JOURNEY,
                subJourney
        );

        bundle.putLong(
                AnalyticsParams.START_TIME_MS,
                System.currentTimeMillis()
        );

        analytics.logEvent(
                AnalyticsEvents.FLOW_START,
                bundle
        );
    }


    /** Evento: click_element */
    public void logClickElement(@NonNull String jornada, @NonNull String subjornada, @NonNull Tela tela,
            @NonNull ElementoId elementoId, @Nullable Object conteudo) {

        Bundle bundle = new Bundle();
        bundle.putString(PARAM_JORNADA_NOME, jornada);
        bundle.putString(PARAM_SUBJORNADA, subjornada);
        bundle.putString(PARAM_TELA_NOME, tela.nome);
        bundle.putString(PARAM_ELEMENTO_ID, elementoId.id);

        if (conteudo != null) {
            if (conteudo instanceof String) {
                bundle.putString(PARAM_CONTEUDO, (String) conteudo);
            } else if (conteudo instanceof Integer) {
                bundle.putInt(PARAM_CONTEUDO, (Integer) conteudo); // útil p/ grade (ex.: número 44)
            } else if (conteudo instanceof Long) {
                bundle.putLong(PARAM_CONTEUDO, (Long) conteudo);
            } else if (conteudo instanceof Double) {
                bundle.putDouble(PARAM_CONTEUDO, (Double) conteudo); // quando conteúdo for um valor monetário
            } else if (conteudo instanceof Float) {
                bundle.putDouble(PARAM_CONTEUDO, ((Float) conteudo).doubleValue());
            } else if (conteudo instanceof Boolean) {
                bundle.putBoolean(PARAM_CONTEUDO, ((Boolean) conteudo));
            } else {
                bundle.putString(PARAM_CONTEUDO, String.valueOf(conteudo));
            }
        }

        analytics.logEvent(EVENT_CLICK_ELEMENT, bundle);
    }

    public void logClickElement(
            @NonNull String jornada,
            @NonNull String subjornada,
            @NonNull Tela tela,
            @NonNull ElementoId elementoId) {

        logClickElement(jornada, subjornada, tela, elementoId, null);
    }


    public Tela getTelaPorModalidade(ModalidadeEnum modalidade) {
        if (modalidade == null) return null;

        switch (modalidade) {
            case MEGA_SENA:
                return Tela.MEGA_SENA_ESCOLHA_NUMEROS;

            case LOTOFACIL:
                return Tela.LOTOFACIL_SENA_ESCOLHA_NUMEROS;

            default:
                return null; // ou uma tela default
        }
    }

}

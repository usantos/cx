package br.gov.caixa.loterias.apostas.utils.shake;

import android.text.TextUtils;

import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;

public final class ApostaShakePreferences {
    private static final String ATIVA = "aposta_shake_ativa";
    private static final String TUTORIAL = "aposta_shake_tutorial_v1_concluido";
    private static final String TOOLTIP = "aposta_shake_tooltip_pendente";
    private static final String AVISO = "aposta_shake_ocultar_aviso";
    private ApostaShakePreferences() { }
    // O SharedPreferences é compartilhado pelo aplicativo: particionar pelo CPF impede
    // que a conta anterior deixe o tutorial concluído ou o shake ligado para outra conta.
    private static String chaveUsuario(String chave) {
        String cpf = DadosUsuarioBO.obterCpf();
        String usuario = TextUtils.isEmpty(cpf) ? "anonimo" : cpf.replaceAll("\\D", "");
        return chave + "_" + usuario;
    }
    public static boolean isTutorialConcluido() { return SharedPreferencesUtils.getValorBoolean(chaveUsuario(TUTORIAL), false); }
    public static void concluirTutorial() {
        SharedPreferencesUtils.setValor(chaveUsuario(TOOLTIP), true);
        SharedPreferencesUtils.setValor(chaveUsuario(TUTORIAL), true);
    }
    public static boolean isTooltipPendente() { return SharedPreferencesUtils.getValorBoolean(chaveUsuario(TOOLTIP), false); }
    public static void consumirTooltip() { SharedPreferencesUtils.setValor(chaveUsuario(TOOLTIP), false); }
    public static boolean isAvisoOculto() { return SharedPreferencesUtils.getValorBoolean(chaveUsuario(AVISO), false); }
    public static void setAvisoOculto(boolean oculto) { SharedPreferencesUtils.setValor(chaveUsuario(AVISO), oculto); }
    public static boolean isAtiva() { return SharedPreferencesUtils.getValorBoolean(chaveUsuario(ATIVA), false); }
    public static void setAtiva(boolean ativa) { SharedPreferencesUtils.setValor(chaveUsuario(ATIVA), ativa); }
}

package br.gov.caixa.loterias.apostas.utils;

public final class ApostasIdenticasSingleton {

    private static final ApostasIdenticasSingleton INSTANCE =
            new ApostasIdenticasSingleton();

    private boolean validacaoInicialPendente;

    private ApostasIdenticasSingleton() {
    }

    public static ApostasIdenticasSingleton getInstance() {
        return INSTANCE;
    }

    public boolean isValidacaoInicialPendente() {
        return validacaoInicialPendente;
    }

    public void setValidacaoInicialPendente(boolean validacaoInicialPendente) {
        this.validacaoInicialPendente = validacaoInicialPendente;
    }
}

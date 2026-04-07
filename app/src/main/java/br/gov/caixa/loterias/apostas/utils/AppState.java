package br.gov.caixa.loterias.apostas.utils;

public final class AppState {
    private AppState() {}
    public static volatile boolean wasInBackground = false;
}

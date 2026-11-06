package br.gov.caixa.loterias.apostas.utils;

public enum IconeAppEnum {

    DEFAULT(".alias.LauncherDefault"),
    DEFAULT_DISABLED(".alias.LauncherDefaultDisabled"),
    OUTUBRO_ROSA(".alias.LauncherOutubroRosa");


    private final String key;

    IconeAppEnum(String key) {
        this.key = key;
    }

    public String get() {
        return key;
    }

}

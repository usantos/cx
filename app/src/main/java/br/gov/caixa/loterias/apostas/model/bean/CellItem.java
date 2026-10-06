package br.gov.caixa.loterias.apostas.model.bean;

public class CellItem {
    private final String value;
    private final String accessibilityLabel;

    public CellItem(String value, String accessibilityLabel) {
        this.value = value;
        this.accessibilityLabel = accessibilityLabel;
    }

    public String getValue() {
        return value;
    }

    public String getAccessibilityLabel(){
        return accessibilityLabel;
    }
}

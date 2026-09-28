package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

/**
 * Created by joafilho on 23/01/2018.
 */

public class Aposta {

    private String value;
    private boolean selected;
    private String label;

    public Aposta() {
    }

    public Aposta(String value, boolean selected) {
        this.value = value;
        this.selected = selected;
        this.label = value;
    }

    public Aposta(String value, boolean selected, String label) {
        this.value = value;
        this.selected = selected;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}

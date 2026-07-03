package br.gov.caixa.loterias.apostas.model.ui;

public class LotteryNumberUiModel {

    private final String number;
    private final boolean rewarded;
    private String accessibility;

    public LotteryNumberUiModel(String number, boolean rewarded, String accessibility) {
        this.number = number;
        this.rewarded = rewarded;
        this.accessibility = accessibility;
    }

    public String getNumber() {
        return number;
    }

    public boolean isRewarded() {
        return rewarded;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }
}

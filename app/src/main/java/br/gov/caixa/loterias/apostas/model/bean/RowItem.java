package br.gov.caixa.loterias.apostas.model.bean;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.ui.LotteryNumberUiModel;

public class RowItem {
    private final List<LotteryNumberUiModel> items;
    private boolean isSuper7 = false;

    public List<LotteryNumberUiModel> getItems() {
        return items;
    }

    public RowItem(List<LotteryNumberUiModel> items) {
        this.items = items;
    }


    public boolean isSuper7() {
        return isSuper7;
    }

    public void setSuper7(boolean bool) {
        this.isSuper7 = bool;
    }

}

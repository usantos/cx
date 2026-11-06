package br.gov.caixa.loterias.apostas.model.bean;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.ui.LotteryNumberUiModel;

public class RowItem {
    private final List<LotteryNumberUiModel> items;

    public List<LotteryNumberUiModel> getItems() {
        return items;
    }

    public RowItem(List<LotteryNumberUiModel> items) {
        this.items = items;
    }

}

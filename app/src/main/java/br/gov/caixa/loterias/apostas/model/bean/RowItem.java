package br.gov.caixa.loterias.apostas.model.bean;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.ui.LotteryNumberUiModel;

public record RowItem(List<LotteryNumberUiModel> items) {}

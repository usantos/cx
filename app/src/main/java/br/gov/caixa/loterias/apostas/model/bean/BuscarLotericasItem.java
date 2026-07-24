package br.gov.caixa.loterias.apostas.model.bean;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaComposition;

public class BuscarLotericasItem implements ListItem{
    private LotericaComposition loterica;

    public BuscarLotericasItem(LotericaComposition loterica) {
        this.loterica = loterica;
    }

    public LotericaComposition getLoterica() {
        return loterica;
    }

    @Override
    public int getType() {
        return 2;
    }
}
